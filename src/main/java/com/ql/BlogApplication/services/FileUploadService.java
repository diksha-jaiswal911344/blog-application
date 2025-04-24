package com.ql.BlogApplication.services;
import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {
    String storeFile(MultipartFile file);
}
