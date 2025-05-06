package com.ql.BlogApplication.services.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.ql.BlogApplication.exceptions.FileUploadException;
import com.ql.BlogApplication.services.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of FileUploadService that uses Amazon S3 for storage
 */
@Service
public class S3FileUploadService implements FileUploadService {

    // List of allowed image file extensions
    private final List<String> ALLOWED_EXTENSIONS = Arrays.asList(".png", ".jpg", ".jpeg", ".gif");
    @Autowired
    private AmazonS3 s3Client;
    @Value("${aws.s3.bucket}")
    private String bucketName;

    /**
     * Uploads a file to Amazon S3 and returns the public URL
     *
     * @param file The MultipartFile to upload
     * @return The public URL of the uploaded file
     * @throws FileUploadException if the file can't be uploaded
     */
    @Override
    public String storeFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new FileUploadException("Cannot upload empty file");
        }

        // Validate file extension
        String fileExtension = getFileExtension(file.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(fileExtension.toLowerCase())) {
            throw new FileUploadException("Only image files (png, jpg, jpeg, gif) are allowed");
        }

        try {
            // Create unique file name to prevent overrides
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            String key = "uploads/" + UUID.randomUUID().toString() + fileExtension;

            // Prepare metadata
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            // Upload file to S3
            s3Client.putObject(new PutObjectRequest(bucketName, key, file.getInputStream(), metadata).withCannedAcl(CannedAccessControlList.PublicRead)); // Make it publicly readable

            // Return the public URL of the file
            return s3Client.getUrl(bucketName, key).toString();

        } catch (IOException e) {
            throw new FileUploadException("Failed to store file to S3", e);
        } catch (Exception e) {
            throw new FileUploadException("Error occurred during file upload: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes a file from Amazon S3
     *
     * @param fileUrl The full URL of the file to delete
     * @return true if deleted successfully
     */
    public boolean deleteFile(String fileUrl) {
        try {
            // Extract the S3 key from the URL
            String key = extractKeyFromUrl(fileUrl);
            if (key == null) {
                return false;
            }

            // Delete the object from S3
            s3Client.deleteObject(new DeleteObjectRequest(bucketName, key));
            return true;
        } catch (Exception e) {
            // Log the error but don't throw to prevent disrupting the main flow
            System.err.println("Error deleting file from S3: " + e.getMessage());
            return false;
        }
    }

    /**
     * Extracts the file extension from a filename
     *
     * @param fileName The filename to extract from
     * @return The file extension with dot (e.g., ".jpg")
     */
    private String getFileExtension(String fileName) {
        if (fileName == null) {
            return "";
        }
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return fileName.substring(lastDotIndex);
    }

    /**
     * Extracts the S3 key from a full S3 URL
     *
     * @param fileUrl The full S3 URL
     * @return The S3 key or null if not valid
     */
    private String extractKeyFromUrl(String fileUrl) {
        if (fileUrl == null || !fileUrl.contains(bucketName)) {
            return null;
        }

        // The key is the part of the URL after the bucket name
        int bucketEndIndex = fileUrl.indexOf(bucketName) + bucketName.length();
        // Account for various URL formats
        if (fileUrl.charAt(bucketEndIndex) == '.') {
            // Format: https://bucket-name.s3.region.amazonaws.com/key
            bucketEndIndex = fileUrl.indexOf('/', bucketEndIndex);
        }

        if (bucketEndIndex >= 0 && bucketEndIndex < fileUrl.length() - 1) {
            return fileUrl.substring(bucketEndIndex + 1);
        }

        return null;
    }
}