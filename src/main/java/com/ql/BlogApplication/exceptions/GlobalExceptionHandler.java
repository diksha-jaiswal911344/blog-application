package com.ql.BlogApplication.exceptions;

import com.ql.BlogApplication.DTO.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Logger for tracking activities in this class
    private static final Logger logger= LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Handle Resource Not Found exceptions
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        logger.error("Resource Not Found: {}", ex.getMessage());

        ApiResponse response = ApiResponse.builder()
                .success(false)
                .Code(HttpStatus.NOT_FOUND.value())
                .message("Resource Not Found")
                .error(ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // Handle Bad Request exceptions
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse> handleBadRequest(BadRequestException ex) {
        logger.warn("Bad Request: {}", ex.getMessage());

        ApiResponse response = ApiResponse.builder()
                .success(false)
                .Code(HttpStatus.BAD_REQUEST.value())
                .message("Bad Request")
                .error(ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Handle validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidationError(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        logger.warn("Validation Failed: {}", errors);

        ApiResponse response = ApiResponse.builder()
                .success(false)
                .Code(HttpStatus.BAD_REQUEST.value())
                .message("Validation Failed")
                .data(errors)
                .error("error occured")// all errors in map
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Handle cases where no handler is found for a request
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse> handleNoHandlerFoundException(NoHandlerFoundException ex) {

        logger.error("No handle found: {}", ex.getRequestURL());
        ApiResponse response = ApiResponse.builder()
                .success(false)
                .Code(HttpStatus.NOT_FOUND.value())
                .message("No handler found for this URL")
                .error(ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}
