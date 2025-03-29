package com.ql.BlogApplication.payloads;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiResponse {
    private boolean success;
    private int statusCode;
    private String message;
    private String error;
    private Object data;  // <--- This will hold User or any data
}
