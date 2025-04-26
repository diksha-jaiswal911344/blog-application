package com.ql.BlogApplication.controllers;

import com.ql.BlogApplication.DTO.*;
//import com.ql.BlogApplication.exceptions.BadRequestException;
import com.ql.BlogApplication.repository.UserRepository;
import com.ql.BlogApplication.services.UserService;
import com.ql.BlogApplication.utils.JwtUtil;
import jakarta.validation.Valid;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ql.BlogApplication.entities.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("api/users")
public class UserController {

//    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserRepository userRepository;

    private final JwtUtil jwtUtil;

    private final UserService userService;

    private OtpVerificationRequestDto otpVerificationRequestDto;

    @Autowired // Inject UserService
    public UserController(UserRepository userRepository, JwtUtil jwtUtil, UserService userService) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @PostMapping(value = "/register")
    public ResponseEntity<ApiResponseNew<Map<String, String>>> createUser(@Valid @RequestBody UserRequestDto dto) {
        UserResponseDto userResponse = userService.createUser(dto);

        Map<String, String> data = new HashMap<>();
        data.put("userId", String.valueOf(userResponse.getId()));
        data.put("email", userResponse.getEmail());
        data.put("message", "User created successfully. OTP sent to email.");

        return new ResponseEntity<>(ApiResponseNew.success(201, data, "Registration successful"), HttpStatus.CREATED);
    }


    //verify otp during registration
    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponseNew<Map<String, String>>> verifyOtp(@RequestBody @Valid OtpVerificationRequestDto otpVerificationRequestDto) {
        String message = userService.verifyOtp(otpVerificationRequestDto);

        // wraping message in a map
        Map<String, String> responseData = new HashMap<>();
        responseData.put("message", message);

        return ResponseEntity.ok(ApiResponseNew.success(200, responseData, "OTP verification successful"));
    }

    @PostMapping("/verify-login-otp")
    public ResponseEntity<ApiResponseNew<Map<String, String>>> verifyLoginOtp(
            @RequestBody @Valid OtpVerificationRequestDto dto) {

        String token = userService.verifyLoginOtp(dto); // update the service method to return String (or a DTO)

        Map<String, String> responseData = new HashMap<>();
        responseData.put("token", token);

        return ResponseEntity.ok(ApiResponseNew.success(200, responseData, "Login OTP verified"));
    }


    //read all users
    @GetMapping
    public ResponseEntity<ApiResponseNew<Map<String, List<UserResponseDto>>>> getAllUsers() {

        List<UserResponseDto> users=userService.getAllUsers();

        Map<String,List<UserResponseDto>> wrappedUsers=new HashMap<>();
        wrappedUsers.put("AvailableUsers",users);
        return ResponseEntity.ok(ApiResponseNew.success(200,wrappedUsers,"fetched all users from database"));
    }

    //otp request FOR login on EMAIL
    @PostMapping("/request-otp")
    public ResponseEntity<ApiResponseNew<Map<String, String>>> requestOtpForLogin(@RequestBody @Valid OtpLoginRequestDto dto) {
        userService.sendOtpForLogin(dto);
        Map<String, String> data = new HashMap<>();
        data.put("message", "OTP sent to your email.");

        return ResponseEntity.ok(ApiResponseNew.success(200, data, "OTP request successful"));
    }


    //read by id
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseNew<UserResponseDto>> getUserById(@PathVariable Long id) {
        UserResponseDto user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponseNew.success(200, user, "User fetched successfully"));
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponseNew<UserResponseDto>> updateUser(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody UserRequestDto userRequestDto) {

        String email = jwtUtil.extractEmail(token.substring(7));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserResponseDto response = userService.updateUser(userRequestDto, user.getId());

        return ResponseEntity.ok(ApiResponseNew.success(200, response, "User updated successfully"));
    }


    //delete
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponseNew<String>> deleteUser(@RequestHeader("Authorization") String token) {
        String email = jwtUtil.extractEmail(token.substring(7));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userService.deleteUser(user.getId()); // Only deleting their own data

        return ResponseEntity.ok(
                ApiResponseNew.success(200, "User deleted successfully", "Operation successful")
        );
    }

    //login api
    @PostMapping("/login")
    public ResponseEntity<ApiResponseNew<String>> loginUser(@RequestBody LoginDto loginDto) {
        String token = userService.loginUser(loginDto);
        return ResponseEntity.ok(
                ApiResponseNew.success(200, token, "Login successful")
        );
    }
}