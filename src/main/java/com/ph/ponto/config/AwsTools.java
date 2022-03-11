package com.ph.ponto.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.ByteArrayInputStream;

@Configuration
public class AwsTools {

    @Value("${aws.key}")
    private String key;

    @Value("${aws.secretKey}")
    private String secretKey;

    @Value("${aws.bucketName}")
    private String bucketName;

    @Value("${aws.region}")
    private String region;

    private AmazonS3 s3Client;

    private BasicAWSCredentials awsCreds;

    @Bean
    @Primary
    public AmazonS3 s3Client() {
        if (s3Client == null) {
            awsCreds = new BasicAWSCredentials(key, secretKey);
            s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion(region)
                    .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                    .build();
        }
        return s3Client;
    }

    public void uploadImage(String fileName, byte[] bytes){
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(bytes.length);
            s3Client.putObject(new PutObjectRequest(bucketName+"/userImages", fileName, inputStream, metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteImage(String fileName){
        try {
            s3Client.deleteObject(bucketName+"/userImages", fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getImage(String fileName){
        try {
            String url = s3Client.getUrl(bucketName+"/userImages", fileName).toString()+".png";
            return url;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

}
