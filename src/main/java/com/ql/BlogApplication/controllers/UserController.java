package com.ql.BlogApplication.controllers;

import com.ql.BlogApplication.DTO.*;
import com.ql.BlogApplication.repository.UserRepository;
import com.ql.BlogApplication.services.UserService;
import com.ql.BlogApplication.utils.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ql.BlogApplication.entities.User;

import java.util.*;


@RestController
@RequestMapping("api/users")
public class UserController {

    private final UserRepository userRepository;

    private final JwtUtil jwtUtil;

    private final UserService userService;

    private OtpVerificationRequestDto otpVerificationRequestDto;

    // Inject UserService
    @Autowired
    public UserController(UserRepository userRepository, JwtUtil jwtUtil, UserService userService) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    // creating/registering user
    @PostMapping(value = "/register")
    public ResponseEntity<ApiResponseNew<Map<String, String>>> createUser(@Valid @RequestBody UserRequestDto dto) {
        ApiResponseNew<Map<String, String>> response = userService.createUser(dto);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getHttpStatusCode()));
    }


    // verify otp during registration
    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponseNew<Map<String, String>>> verifyOtp(@RequestBody @Valid OtpVerificationRequestDto otpVerificationRequestDto) {
        ApiResponseNew<Map<String, String>> response = userService.verifyOtp(otpVerificationRequestDto);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getHttpStatusCode()));
    }

    //verify otp during login
    @PostMapping("/verify-login-otp")
    public ResponseEntity<ApiResponseNew<Map<String, String>>> verifyLoginOtp(@RequestBody @Valid OtpVerificationRequestDto dto) {
        ApiResponseNew<Map<String, String>> response = userService.verifyLoginOtp(dto);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getHttpStatusCode()));
    }


    //read all users
    @GetMapping
    public ResponseEntity<ApiResponseNew<List<UserResponseDto>>> getAllUsers() {
        ApiResponseNew<List<UserResponseDto>> response = userService.getAllUsers();
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getHttpStatusCode()));
    }

    //otp request FOR login on EMAIL
    @PostMapping("/request-otp")
    public ResponseEntity<ApiResponseNew<Map<String, String>>> requestOtpForLogin(@RequestBody @Valid OtpLoginRequestDto dto) {
        ApiResponseNew<Map<String, String>> response = userService.sendOtpForLogin(dto);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getHttpStatusCode()));
    }

    //read by id
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseNew<UserResponseDto>> getUserById(@PathVariable Long id) {
        ApiResponseNew<UserResponseDto> response = userService.getUserById(id);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getHttpStatusCode()));
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponseNew<UserResponseDto>> updateUser(@RequestHeader("Authorization") String token, @Valid @RequestBody UserRequestDto userRequestDto) {

        // Extract token and validate
        if (token == null || !token.startsWith("Bearer ")) {
            ApiResponseNew<UserResponseDto> errorResponse = ApiResponseNew.success(401, false, "Invalid or missing authorization token", null);
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }

        try {
            String email = jwtUtil.extractEmail(token.substring(7));
            Optional<User> userOptional = userRepository.findByEmail(email);

            if (userOptional.isEmpty()) {
                ApiResponseNew<UserResponseDto> errorResponse = ApiResponseNew.success(404, false, "User not found with provided token", null);
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }

            User user = userOptional.get();
            ApiResponseNew<UserResponseDto> response = userService.updateUser(userRequestDto, user.getId());
            return new ResponseEntity<>(response, HttpStatus.valueOf(response.getHttpStatusCode()));

        } catch (Exception e) {
            ApiResponseNew<UserResponseDto> errorResponse = ApiResponseNew.success(401, false, "Invalid token", null);
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }
    }

    //for deleting the logedin user
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponseNew<Map<String, String>>> deleteUser(@RequestHeader("Authorization") String token) {
        // Extract token and validate
        if (token == null || !token.startsWith("Bearer ")) {
            ApiResponseNew<Map<String, String>> errorResponse = ApiResponseNew.success(401, false, "Invalid or missing authorization token", Collections.emptyMap());
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }

        try {
            String email = jwtUtil.extractEmail(token.substring(7));
            Optional<User> userOptional = userRepository.findByEmail(email);

            if (userOptional.isEmpty()) {
                ApiResponseNew<Map<String, String>> errorResponse = ApiResponseNew.success(404, false, "User not found with provided token", Collections.emptyMap());
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }

            User user = userOptional.get();
            ApiResponseNew<Map<String, String>> response = userService.deleteUser(user.getId());
            return new ResponseEntity<>(response, HttpStatus.valueOf(response.getHttpStatusCode()));

        } catch (Exception e) {
            ApiResponseNew<Map<String, String>> errorResponse = ApiResponseNew.success(401, false, "Invalid token", Collections.emptyMap());
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }
    }

    //login api
    @PostMapping("/login")
    public ResponseEntity<ApiResponseNew<Map<String, String>>> loginUser(@RequestBody LoginDto loginDto) {
        ApiResponseNew<Map<String, String>> response = userService.loginUser(loginDto);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getHttpStatusCode()));
    }
}

