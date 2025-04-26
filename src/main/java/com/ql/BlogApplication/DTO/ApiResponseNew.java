package com.ql.BlogApplication.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponseNew<T>  {
    private boolean success;
    private int code;
    private String message;
    private T error;
    private T data;// <--- This will hold User or any data

    public static <T> ApiResponseNew<T> success(int code, T data, String message) {
        return ApiResponseNew.<T>builder()
                .success(true)
                .code(code)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponseNew<T> error(int code, T error, String message) {
        return ApiResponseNew.<T>builder()
                .success(false)
                .code(code)
                .message(message)
                .error(error)
                .build();
    }
}
