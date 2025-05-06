package com.ql.BlogApplication.config;

import com.ql.BlogApplication.services.FileUploadService;
import com.ql.BlogApplication.services.impl.FileUploadServiceImpl;
import com.ql.BlogApplication.services.impl.S3FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configuration class that determines which file upload service implementation to use
 */
@Configuration
public class UploadServiceConfig {

    @Value("${storage.service:s3}")
    private String storageService;

    @Autowired
    private S3FileUploadService s3FileUploadService;

    @Autowired
    private FileUploadServiceImpl localFileUploadService;

    /**
     * Creates the primary FileUploadService bean based on configuration
     * @return The appropriate FileUploadService implementation
     */
    @Bean
    @Primary
    public FileUploadService fileUploadService() {
        // Select which implementation to use based on configuration
        if ("local".equals(storageService)) {
            System.out.println("Using LOCAL file storage implementation");
            return localFileUploadService;
        } else {
            System.out.println("Using S3 file storage implementation");
            return s3FileUploadService;
        }
    }
}