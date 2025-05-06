package com.ql.BlogApplication.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.Collections;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)

public class ApiResponseNew<T> {
    private int code;
    private boolean success;
    private String message;
    //    private T error;
    @Builder.Default
    private Object data = Collections.emptyMap();

    @JsonIgnore
    private int httpStatusCode;

    public static <T> ApiResponseNew<T> success(int code, boolean success, String message, Object data) {
        return ApiResponseNew.<T>builder()
                .code(code)
                .success(success)
                .message(message)
                .data(data)
                .httpStatusCode(success ? 200 : 400)
                .build();
    }
}
//    public static <T> ApiResponseNew<T> error(int code, T error, String message) {
//        return ApiResponseNew.<T>builder()
//                .success(false)
//                .code(code)
//                .message(message)
//                .error(error)
//                .build();
//    }

