package FileSync;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;


public class FileSync {

    public static void main(String[] args) {
        ThreadPoolExecutor threadPool=new ThreadPoolExecutor(3, 10, 30, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());
        final String path="D:\\data\\";
        S3tools.Download(path);
        WatchService watchService= null;
        final CopyOnWriteArraySet<String> Monitor=new CopyOnWriteArraySet<>();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Monitor.clear();
            }
        }, 0,1000);
        try {
            watchService = FileSystems.getDefault().newWatchService();
            Path dir = Paths.get(path);
            dir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY,
                    StandardWatchEventKinds.ENTRY_DELETE);
            while (true){
                WatchKey key=watchService.take();
                List<WatchEvent<?>> eventList=key.pollEvents();
                for(final WatchEvent<?> event:eventList){
                    if(event.kind().equals(StandardWatchEventKinds.ENTRY_CREATE)){
                        if(!Monitor.contains(path+event.context())) {
                            Monitor.add(path+event.context());
                            System.out.println("create file "+event.context()+" ,start to sync");
                            FutureTask<Boolean> task = new FutureTask<>(new Callable<Boolean>() {
                                @Override
                                public Boolean call() {
                                    return S3tools.Upload(path + event.context());
                                }
                            });
                            threadPool.execute(task);
                        }
                    }else if(event.kind().equals(StandardWatchEventKinds.ENTRY_DELETE)){
                        System.out.println("delete file "+event.context()+" ,start remote sync");
                        FutureTask<Boolean> task=new FutureTask<>(new Callable<Boolean>() {
                            @Override
                            public Boolean call() {
                                return S3tools.Delete(path+event.context());
                            }
                        });
                        threadPool.execute(task);
                    }else if(event.kind().equals(StandardWatchEventKinds.ENTRY_MODIFY)){
                        if(!Monitor.contains(path+event.context())) {
                            Monitor.add(path+event.context());
                            System.out.println("modify file " + event.context() + " ,start remote sync");
                            FutureTask<Boolean> task = new FutureTask<>(new Callable<Boolean>() {
                                @Override
                                public Boolean call() {
                                    return S3tools.Upload(path + event.context());
                                }
                            });
                            threadPool.execute(task);
                        }
                    }
                }
                key.reset();
            }
        }catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally{
            threadPool.shutdown();
            threadPool=null;
            watchService=null;
            System.gc();
        }

    }


}