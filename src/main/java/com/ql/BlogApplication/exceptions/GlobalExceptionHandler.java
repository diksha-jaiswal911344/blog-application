package com.ql.BlogApplication.exceptions;

import com.ql.BlogApplication.DTO.ApiResponseNew;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.nio.file.AccessDeniedException;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handle Resource Not Found Exception (we'll still catch it even if services don't throw it)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponseNew<Object>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        Map<String, String> errorDetails = new HashMap<>();
        ApiResponseNew<Object> response = ApiResponseNew.success(404, false, ex.getMessage(), errorDetails);

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // Handle constraint violations
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponseNew<Object>> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> errors = ex.getConstraintViolations().stream().collect(Collectors.toMap(violation -> violation.getPropertyPath().toString(), ConstraintViolation::getMessage, (existing, replacement) -> existing + ", " + replacement));

        ApiResponseNew<Object> response = ApiResponseNew.success(400, false, "Constraint violation", errors);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Handle data integrity violations (e.g., unique constraint violations)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponseNew<Object>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        ApiResponseNew<Object> response = ApiResponseNew.success(409, false, "Data integrity violation. Possible duplicate or invalid data.", Collections.singletonMap("error", ex.getMostSpecificCause().getMessage()));

        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    // Handle access denied exceptions
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponseNew<Object>> handleAccessDeniedException(AccessDeniedException ex) {
        ApiResponseNew<Object> response = ApiResponseNew.success(403, false, "Access denied", Collections.singletonMap("error", ex.getMessage()));

        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }


    // Handle all other exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseNew<Object>> handleGlobalException(Exception ex, WebRequest request) {
        ApiResponseNew<Object> response = ApiResponseNew.success(500, false, "An unexpected error occurred", Collections.singletonMap("error", ex.getMessage()));

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //When a required query parameter or form parameter is missing
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponseNew<Map<String, String>>> handleMissingRequestParamException(MissingServletRequestParameterException ex, HttpServletRequest request) {
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("parameter", ex.getParameterName()); // which parameter was missing
        errorDetails.put("path", request.getRequestURI());    // API path
        errorDetails.put("timestamp", Instant.now().toString());
        errorDetails.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponseNew.success(HttpStatus.BAD_REQUEST.value(), false, "Missing required request parameter: " + ex.getParameterName(), errorDetails));
    }

    //when a required path variable is missing in a controller method
    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<ApiResponseNew<Map<String, String>>> handleMissingPathVariable(MissingPathVariableException ex, HttpServletRequest request) {
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("path", request.getRequestURI());                            // The endpoint hit
        errorDetails.put("timestamp", Instant.now().toString());                      // Current time
        errorDetails.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());         // "Bad Request"

        // Add the name of the missing variable
        errorDetails.put("missingPathVariable", ex.getVariableName());

        // Return structured error response
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponseNew.success(HttpStatus.BAD_REQUEST.value(), false, "Missing path variable: " + ex.getVariableName(), errorDetails));
    }

    //This exception is thrown when validation on a request body annotated with @Valid fails
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseNew<Map<String, String>>> handleValidationErrorsException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("path", request.getRequestURI());
        errorDetails.put("timestamp", Instant.now().toString());
        errorDetails.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());

        // Add field-specific validation errors
        ex.getBindingResult().getFieldErrors().forEach(fieldError -> errorDetails.put(fieldError.getField(), fieldError.getDefaultMessage()));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponseNew.success(HttpStatus.BAD_REQUEST.value(), false, "Validation failed for request body", errorDetails));
    }

    //When the request body is missing or contains malformed/invalid JSON that cannot be deserialized
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponseNew<Map<String, String>>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, HttpServletRequest request) {
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("path", request.getRequestURI());
        errorDetails.put("timestamp", Instant.now().toString());
        errorDetails.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());

        // Return structured ApiResponseNew
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponseNew.success(HttpStatus.BAD_REQUEST.value(), false, "Invalid or malformed JSON request body", errorDetails));
    }

    //Request parameter or path variable can't be converted to the required data type
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponseNew<Map<String, String>>> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("path", request.getRequestURI());                                      // URI that failed
        errorDetails.put("timestamp", Instant.now().toString());                                // When it failed
        errorDetails.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());                   // "Bad Request"
        errorDetails.put("parameterName", ex.getName());                                        // e.g. "id"
        errorDetails.put("invalidValue", String.valueOf(ex.getValue()));                        // e.g. "abc"
        errorDetails.put("expectedType", ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "Unknown");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponseNew.success(HttpStatus.BAD_REQUEST.value(), false, "Invalid type for parameter: " + ex.getName(), errorDetails));
    }

    //Request using an HTTP method (GET, POST, PUT, DELETE, etc.) that is not supported by the endpoint
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponseNew<Map<String, String>>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("path", request.getRequestURI());
        errorDetails.put("timestamp", Instant.now().toString());
        errorDetails.put("error", HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase());
        errorDetails.put("unsupportedMethod", ex.getMethod());
        errorDetails.put("supportedMethods", String.join(", ", ex.getSupportedMethods()));

        // Return response in consistent format
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(ApiResponseNew.success(HttpStatus.METHOD_NOT_ALLOWED.value(), false, "HTTP method not supported for this endpoint", errorDetails));
    }

    //Content-Type (e.g., application/json, multipart/form-data, etc.) of the request is not supported by the API endpoint
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiResponseNew<Map<String, String>>> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("path", request.getRequestURI());
        errorDetails.put("timestamp", Instant.now().toString());
        errorDetails.put("error", HttpStatus.UNSUPPORTED_MEDIA_TYPE.getReasonPhrase());        // Unsupported Media Type
        errorDetails.put("unsupportedMediaType", ex.getContentType() != null ? ex.getContentType().toString() : "Unknown");
        errorDetails.put("supportedMediaTypes", ex.getSupportedMediaTypes().toString());       // Allowed types
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(ApiResponseNew.success(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), false, "Content-Type not supported", errorDetails));
    }
}