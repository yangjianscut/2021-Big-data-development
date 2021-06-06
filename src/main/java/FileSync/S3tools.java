package FileSync;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class S3tools {
    private final static String bucketName = "yangjian";
    private final static String accessKey = "0FE7F0CC27B2127512BC";
    private final static String secretKey = "Wzg0OUVDMEZGN0JEQUMzQjJDMUVDNjQ5N0RDM0U4";
    private final static String serviceEndpoint = "http://scut.depts.bingosoft.net:29997/";
    private final static String signingRegion = "";
    private static long partSize = 5 << 20;
    private final static BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey,secretKey);
    private final static ClientConfiguration ccfg = new ClientConfiguration().
            withUseExpectContinue(true);
    private final static AwsClientBuilder.EndpointConfiguration endpoint = new AwsClientBuilder.EndpointConfiguration(serviceEndpoint, signingRegion);

    private final static AmazonS3 s3 = AmazonS3ClientBuilder.standard()
            .withCredentials(new AWSStaticCredentialsProvider(credentials))
            .withClientConfiguration(ccfg)
            .withEndpointConfiguration(endpoint)
            .withPathStyleAccessEnabled(true)
            .build();

    public static Boolean Upload(String filePath) {
        final String keyName = Paths.get(filePath).getFileName().toString();
        final File file = new File(filePath);
        if(file.isDirectory()){
            System.out.println("create file"+keyName+" ,start to upload");
            ObjectMetadata Metadata=new ObjectMetadata();
            Metadata.setContentLength(0);
            InputStream empty=new ByteArrayInputStream(new byte[0]);
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName,keyName+"/",empty,Metadata);
            s3.putObject(putObjectRequest );
            System.out.println("already in s3 "+bucketName+" create dict "+keyName);
            return true;
        }
        System.out.println("start to S3 " + bucketName + " upload file " + keyName);
        if(file.length() > 20 << 20) {
            long partSize = 5 << 20;
            ArrayList<PartETag> partETags = new ArrayList<PartETag>();
            long contentLength = file.length();
            String uploadId = null;
            try {
                // Step 1: Initialize.
                InitiateMultipartUploadRequest initRequest =
                        new InitiateMultipartUploadRequest(bucketName, keyName);
                uploadId = s3.initiateMultipartUpload(initRequest).getUploadId();
                System.out.format("Created upload ID was %s\n", uploadId);

                // Step 2: Upload parts.
                long filePosition = 0;
                for (int i = 1; filePosition < contentLength; i++) {
                    // Last part can be less than 5 MB. Adjust part size.
                    partSize = Math.min(partSize, contentLength - filePosition);

                    // Create request to upload a part.
                    UploadPartRequest uploadRequest = new UploadPartRequest()
                            .withBucketName(bucketName)
                            .withKey(keyName)
                            .withUploadId(uploadId)
                            .withPartNumber(i)
                            .withFileOffset(filePosition)
                            .withFile(file)
                            .withPartSize(partSize);

                    System.out.format("Uploading part %d\n", i);
                    partETags.add(s3.uploadPart(uploadRequest).getPartETag());
                    filePosition += partSize;
                }

                System.out.println("Completing upload");
                CompleteMultipartUploadRequest compRequest =
                        new CompleteMultipartUploadRequest(bucketName, keyName, uploadId, partETags);

                s3.completeMultipartUpload(compRequest);

            } catch (Exception e) {
                System.err.println(e.toString());
                if (uploadId != null && !uploadId.isEmpty()) {
                    System.out.println("Aborting upload");
                    s3.abortMultipartUpload(new AbortMultipartUploadRequest(bucketName, keyName, uploadId));
                }
                System.exit(1);
            }
            System.out.println("Done!");
        }else {
            try {
                s3.putObject(bucketName, keyName, file);

            } catch (AmazonServiceException e) {
                if (e.getErrorCode().equalsIgnoreCase("cannot find bucket " + bucketName)) {
                    s3.createBucket(bucketName);
                }

                System.err.println(e.toString());
                System.exit(1);
            } catch (AmazonClientException e) {
                try {
                    s3.getBucketAcl(bucketName);
                } catch (AmazonServiceException ase) {
                    if (ase.getErrorCode().equalsIgnoreCase("cannot find bucket " + bucketName)) {
                        s3.createBucket(bucketName);
                    }
                } catch (Exception ignore) {
                }

                System.err.println(e.toString());
                System.exit(1);
                return false;
            }
            System.out.println("Upload file to S3 " + bucketName  + keyName + " finished ");
        }
        return true;
    }

    public static Boolean DeleteFile(String filePath) {
        final String keyName = Paths.get(filePath).getFileName().toString();
        System.out.println("begin delete S3 file "+bucketName+"/ "+keyName);
        try {
            s3.deleteObject(bucketName, keyName);
        } catch (AmazonServiceException e) {
            try {
                // detect bucket whether exists
                s3.getBucketAcl(bucketName);
            } catch (AmazonServiceException ase) {
                if (ase.getErrorCode().equalsIgnoreCase("cannot find bucket "+bucketName)) {
                    s3.createBucket(bucketName);
                }
            } catch (Exception ignore) {
            }
            System.err.println(e.toString());
            System.exit(1);
            return false;
        }
        System.out.println("From S3 "+bucketName+" delete file "+keyName+" finished");
        return true;
    }

    public static Boolean Download(String filePath){
        ListObjectsV2Result result = s3.listObjectsV2(bucketName);
        List<S3ObjectSummary> objects = result.getObjectSummaries();

        S3ObjectInputStream s3is = null;
        FileOutputStream fos = null;
        String keyName = null;
        System.out.println("start to sync");
        for (S3ObjectSummary object : objects) {
            keyName = object.getKey();
            System.out.println("start to sync" + keyName);
            try {
                S3Object o = s3.getObject(bucketName, keyName);
                s3is = o.getObjectContent();
                if(keyName.endsWith("/")){
                    File dir = new File(filePath + keyName);
                    if(!dir.exists()){
                        dir.mkdir();
                    }
                    continue;
                }
                fos = new FileOutputStream(new File(filePath+keyName));
                if(o.getObjectMetadata().getInstanceLength()> 5<<20){
                    try {
                        // Step 1: Initialize.
                        ObjectMetadata oMetaData = s3.getObjectMetadata(bucketName, keyName);
                        final long contentLength = oMetaData.getContentLength();
                        final GetObjectRequest downloadRequest =
                                new GetObjectRequest(bucketName, keyName);
                        long partSize=5<<20;
                        // Step 2: Download parts.
                        long filePosition = 0;
                        for (int i = 1; filePosition < contentLength; i++) {
                            // Last part can be less than 5 MB. Adjust part size.
                            partSize = Math.min(partSize, contentLength - filePosition);

                            // Create request to download a part.
                            downloadRequest.setRange(filePosition, filePosition + partSize);
                            o = s3.getObject(downloadRequest);

                            // download part and save to local file.
                            System.out.format("Downloading part %d\n", i);

                            filePosition += partSize+1;
                            s3is = o.getObjectContent();
                            byte[] read_buf = new byte[1024 * 1024];
                            int read_len = 0;
                            while ((read_len = s3is.read(read_buf)) > 0) {
                                fos.write(read_buf, 0, read_len);
                            }
                        }

                        // Step 3: Complete.
                        System.out.println("Completing download");

                        System.out.format("save %s to %s\n", keyName, filePath);
                    } catch (Exception e) {
                        System.err.println(e.toString());

                        System.exit(1);
                    }
                    System.out.println("Done!");
                }else{
                    byte[] read_buf = new byte[1024 * 1024];
                    int read_len = 0;
                    while ((read_len = s3is.read(read_buf)) > 0) {
                        fos.write(read_buf, 0, read_len);
                    }
                }
            } catch (AmazonServiceException e) {
                System.err.println(e.toString());
                System.exit(1);
                return false;
            } catch (IOException e) {
                System.err.println(e.getMessage());
                System.exit(1);
                return false;
            } finally {
                if (s3is != null) try { s3is.close(); } catch (IOException e) { }
                if (fos != null) try { fos.close(); } catch (IOException e) { }
            }

            System.out.println("file "+keyName+" sync complete");
        }
        System.out.println("sync complete!");
        return true;
    }

    public static Boolean Delete(String filePath) {
        final String keyName = Paths.get(filePath).getFileName().toString();
        System.out.println("start delete "+bucketName+" from s3 "+keyName);
        try {
            s3.deleteObject(bucketName, keyName);
        } catch (AmazonServiceException e) {
            try {
                // detect bucket whether exists
                s3.getBucketAcl(bucketName);
            } catch (AmazonServiceException ase) {
                if (ase.getErrorCode().equalsIgnoreCase("cannot find the bucket "+bucketName)) {
                    s3.createBucket(bucketName);
                }
            } catch (Exception ignore) {
            }
            System.err.println(e.toString());
            System.exit(1);
            return false;
        }
        System.out.println("delete file from  "+bucketName+keyName+" complete!");
        return true;
    }

}

