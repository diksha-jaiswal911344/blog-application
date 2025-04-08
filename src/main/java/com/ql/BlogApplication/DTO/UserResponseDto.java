package com.ql.BlogApplication.DTO;

import lombok.Data;

@Data
public class UserResponseDto {
    private Long id;
    private String name;
    private String email;
    private String roleName;
}
