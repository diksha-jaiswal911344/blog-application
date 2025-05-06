package com.ql.BlogApplication.exceptions;

/**
 * Exception thrown when file upload operations fail
 */
public class FileUploadException extends RuntimeException {

    /**
     * Constructs a new file upload exception with the specified detail message
     * @param message the detail message
     */
    public FileUploadException(String message) {
        super(message);
    }

    /**
     * Constructs a new file upload exception with the specified detail message and cause
     * @param message the detail message
     * @param cause the cause
     */
    public FileUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}