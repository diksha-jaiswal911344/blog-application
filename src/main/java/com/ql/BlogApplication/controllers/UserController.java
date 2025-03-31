package com.ql.BlogApplication.controllers;

import com.ql.BlogApplication.exceptions.BadRequestException;
import com.ql.BlogApplication.payloads.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ql.BlogApplication.entities.User;


@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    // dummy GET
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getUser(@PathVariable int id) {
        logger.info("Fetching user with ID: {}", id);

        // Simulating a user fetch from DB (we will replace this with actual DB logic later)
        User user = User.builder()
                .id(id)
                .name("acv")
                .email("test@example.com")
                .build();
        // Preparing API response
        ApiResponse response = ApiResponse.builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message("User found successfully")
                .data(user)
                .build();

            logger.info("User found: {}", user);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // dummy POST to trigger MethodArgumentNotValidException
    @PostMapping("/")
    public ResponseEntity<ApiResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        logger.info("Creating user with Name: {}, Email:{}",request.getName(),request.getEmail());

        if ("error".equalsIgnoreCase(request.getName())) {
            throw new BadRequestException("Bad request: name cannot be 'error'");
        }

        // just for testing
        logger.info("User created successfully");
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .statusCode(201)
                .message("User created successfully")
                .build());
    }

    // Inner class just for testing validation
    public static class CreateUserRequest {
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


}
