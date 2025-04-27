package com.ql.BlogApplication.DTO;

import lombok.Data;

@Data
public class UserResponseDto {
    private String id;
    private String name;
    private String email;
    private String roleName;
    private Boolean emailVerified;
}
