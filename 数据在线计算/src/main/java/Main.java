import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.apache.flink.api.common.serialization.SimpleStringSchema;

import org.apache.flink.shaded.guava18.com.google.common.collect.Lists;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.api.windowing.windows.Window;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.util.Collector;
import scala.Serializable;

import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

public class Main {
    private static String accessKey = "64ABEA89A8CFB8297072";
    private static String secretKey = "W0Y0ODdDN0I3MjkwNDcyQTlBODU4REE4OEMzNDRC";
    //s3地址
    private static String endpoint = "http://scut.depts.bingosoft.net:29997";
    //上传到的桶
    private static String bucket = "xuhang";
    //上传文件的路径前缀
    private static String keyPrefix = "upload/";
    //上传数据间隔 单位毫秒
    private static Integer period = 5000;
    //输入的kafka主题名称
    private static String inputTopic = "xh_buy_ticket_3";
    //kafka地址
    private static String bootstrapServers = "bigdata35.depts.bingosoft.net:29035,bigdata36.depts.bingosoft.net:29036,bigdata37.depts.bingosoft.net:29037";
    //    private static String filePath = "D:/STUDY/大三下/大数据实训/class3/outputData2";
    public static class myWindowFunction implements WindowFunction<buy_record, String, String, TimeWindow>{

        @Override
        public void apply(String s, TimeWindow timeWindow, Iterable<buy_record> iterable, Collector<String> collector) throws Exception {
            List<buy_record> records = Lists.newArrayList(iterable);

            if(records.size()>0){
                System.out.println("60秒总共收到的条数："+records.size());
                collector.collect(records.toString());
            }
        }
    }

    public static void main(String[] args) throws Exception{
        //读入s3文件
        String s3Content = s3_to_kafka.readFile();
        s3_to_kafka.produceToKafka(s3Content);
        //构建流执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        //kafka
        Properties kafkaProperties = new Properties();
        kafkaProperties.put("bootstrap.servers", bootstrapServers);
        kafkaProperties.put("group.id", UUID.randomUUID().toString());
        kafkaProperties.put("auto.offset.reset", "earliest");
        kafkaProperties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        kafkaProperties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        DataStreamSource<String> dataStreamSource = env.addSource(new FlinkKafkaConsumer010<String>(
                inputTopic,
                new SimpleStringSchema(),
                kafkaProperties
        ));
        DataStream<buy_record> dataStream = dataStreamSource.map(x->JSONObject.parseObject(x, buy_record.class));
        dataStream.keyBy(v->v.getDestination()).timeWindow(Time.seconds(60)).apply(new myWindowFunction()).writeUsingOutputFormat(new S3Writer(accessKey, secretKey, endpoint, bucket, keyPrefix, period));

        env.execute();
    }

}