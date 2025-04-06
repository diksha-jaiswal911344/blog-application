package com.ql.BlogApplication.controllers;

import com.ql.BlogApplication.DTO.UserRequestDto;
import com.ql.BlogApplication.exceptions.BadRequestException;
import com.ql.BlogApplication.DTO.ApiResponse;
import com.ql.BlogApplication.services.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ql.BlogApplication.entities.User;


@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);


    private final UserService userService;

    @Autowired // Inject UserService
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getUser(@PathVariable Long id) {
        logger.info("Fetching user with ID: {}", id);

//        // Simulating a user fetch from DB (we will replace this with actual DB logic later)
//        User user = User.builder()
//                .id(id)
//                .name("acv")
//                .email("test@example.com")
//                .build();
        User user = userService.getUserById(id);
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
    @PostMapping("/cre")
    public ResponseEntity<ApiResponse> createUser(@Valid @RequestBody UserRequestDto request) {
        logger.info("Creating user with Name: {}, Email:{}, Role:{}",request.getName(),request.getEmail(),request.getRoleName());

        if ("error".equalsIgnoreCase(request.getName())) {
            throw new BadRequestException("Bad request: name cannot be 'error'");
        }

        User newUser = userService.createUser(request);

        // just for testing
        logger.info("User created successfully");
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .Code(201)
                .data(newUser)
                .message("User created successfully")
                .build());
    }
}
