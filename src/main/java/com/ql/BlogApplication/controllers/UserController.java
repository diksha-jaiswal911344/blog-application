package com.ql.BlogApplication.controllers;

import com.ql.BlogApplication.DTO.UserRequestDto;
import com.ql.BlogApplication.exceptions.BadRequestException;
import com.ql.BlogApplication.DTO.ApiResponse;
import jakarta.validation.Valid;
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
                .Code(HttpStatus.OK.value())
                .message("User found successfully")
                .data(user)
                .build();

            logger.info("User found: {}", user);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // dummy POST to trigger MethodArgumentNotValidException
    @PostMapping("/")
    public ResponseEntity<ApiResponse> createUser(@Valid @RequestBody UserRequestDto request) {
        logger.info("Creating user with Name: {}, Email:{}",request.getName(),request.getEmail());

        if ("error".equalsIgnoreCase(request.getName())) {
            throw new BadRequestException("Bad request: name cannot be 'error'");
        }

        // just for testing
        logger.info("User created successfully");
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .Code(201)
                .message("User created successfully")
                .build());
    }




}
