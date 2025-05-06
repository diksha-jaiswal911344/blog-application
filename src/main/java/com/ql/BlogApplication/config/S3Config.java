package com.ql.BlogApplication.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for AWS S3 client setup
 */
@Configuration
public class S3Config {

    @Value("${aws.access.key}")
    private String accessKey;

    @Value("${aws.secret.key}")
    private String secretKey;

    @Value("${aws.region}")
    private String region;

    /**
     * Creates and configures the Amazon S3 client bean
     * @return configured AmazonS3 client
     */
    @Bean
    public AmazonS3 s3Client() {
        // Create AWS credentials using access and secret keys
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

        // Build and return an Amazon S3 client
        return AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.fromName(region))
                .build();
    }
}