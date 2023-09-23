package cn.honorsgc.honorv2.oss;


import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

@Service
@Slf4j
public class OssClient {
    private static final String HTTP_PROTOCOL = "http";
    private final AmazonS3 s3Client;

    @Value("${s3.bucket}")
    private String bucket;

    public OssClient(@Value("${s3.endpoint}") String endpoint,
                     @Value("${s3.access-key}") String accessKey,
                     @Value("${s3.secret-key}") String secretKey
    ) throws MalformedURLException, NoSuchAlgorithmException, KeyManagementException {
        s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(
                        endpoint,
                        ""))
                .withPathStyleAccessEnabled(false)
                .withChunkedEncodingDisabled(true)
                .build();
    }

    public String upload(String objectName, InputStream input, Long size) {
        try {
            log.info("trying to upload " + objectName + " to " + bucket);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(size);
            s3Client.putObject(bucket, objectName, input, metadata);
            long expirationMillis = System.currentTimeMillis() + 24 * 60 * 60 * 1000; // 24小时的毫秒数
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucket, objectName);
            request.setExpiration(new Date(expirationMillis));
            URL url = s3Client.generatePresignedUrl(request);
            log.info("upload success: " + url.toString());
            return url.toString();
        } catch (AmazonServiceException e) {
            e.printStackTrace();
            return null;
        }
    }
}
