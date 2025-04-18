package com.ql.BlogApplication.DTO;

import lombok.Data;

@Data
public class UserResponseDto {
    private long id;
    private String name;
    private String email;
    private String roleName;
}
