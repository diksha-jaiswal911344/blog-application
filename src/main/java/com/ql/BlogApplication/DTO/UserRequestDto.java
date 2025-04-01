package com.ql.BlogApplication.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// Inner class just for testing validation
public class UserRequestDto {
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Name must contain only alphabets")
    @NotEmpty(message = "Name must not be empty")
    @Size(min = 3, max = 100, message = "Name must be between 3 to 10 characters")
    private String name;

    @Email(message = "Email should be valid")
    @NotEmpty(message = "Email must not be empty")
    private String email;

    // getter & setter
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
