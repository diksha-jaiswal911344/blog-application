package com.ql.BlogApplication.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class OtpLoginRequestDto {
    @Email(message = "Email should be valid")
    @NotEmpty(message = "Email must not be empty")
    private String email;
}
