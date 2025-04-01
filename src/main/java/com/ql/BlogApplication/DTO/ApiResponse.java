package com.ql.BlogApplication.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiResponse {
    private boolean success;
    private int Code;
    private String message;
    private String error;
    private Object data;  // <--- This will hold User or any data
}
