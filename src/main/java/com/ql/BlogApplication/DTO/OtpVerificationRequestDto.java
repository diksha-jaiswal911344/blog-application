package com.ql.BlogApplication.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NonNull;

@Data
public class OtpVerificationRequestDto {
    @NotEmpty(message = "email is required")
    @Email(message = "enter a valid email")
    private String email;

    @NotEmpty(message = "otp is required")
    private String otp;
}
