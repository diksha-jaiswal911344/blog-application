package com.ql.BlogApplication.controllers;

import com.ql.BlogApplication.DTO.*;
import com.ql.BlogApplication.exceptions.BadRequestException;
import com.ql.BlogApplication.repository.UserRepository;
import com.ql.BlogApplication.services.UserService;
import com.ql.BlogApplication.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ql.BlogApplication.entities.User;

import java.util.List;


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

    //create user
    @PostMapping(value = "/register")
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserRequestDto dto) {
        return new ResponseEntity<>(userService.createUser(dto), HttpStatus.CREATED);
    }

    //verify otp during registration
    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody @Valid OtpVerificationRequestDto otpVerificationRequestDto) {
        String message = userService.verifyOtp(otpVerificationRequestDto);
        return ResponseEntity.ok(message);
    }

    //verify login otp
    @PostMapping("/verify-login-otp")
    public ResponseEntity<ApiResponse> verifyLoginOtp(@RequestBody @Valid OtpVerificationRequestDto dto) {
        ApiResponse apiResponse = userService.verifyLoginOtp(dto);
        return ResponseEntity.ok(apiResponse);
    }
    //read all users
    @GetMapping
    public List<UserResponseDto> getAllUsers() {
        return userService.getAllUsers();
    }

    //FOR login otp request on EMAIL
    @PostMapping("/request-otp")
    public ResponseEntity<String> requestOtpForLogin(@RequestBody @Valid OtpLoginRequestDto dto) {
        userService.sendOtpForLogin(dto);
        return ResponseEntity.ok("OTP sent to your email.");

    }

    //read by id
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@RequestHeader("Authorization") String token,
                                        @Valid @RequestBody UserRequestDto userRequestDto) {

        String email = jwtUtil.extractEmail(token.substring(7));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserResponseDto response = userService.updateUser(userRequestDto, user.getId());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //delete
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser(@RequestHeader("Authorization") String token) {
        String email = jwtUtil.extractEmail(token.substring(7));

        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("user not found"));
        userService.deleteUser(user.getId()); //only deleting own d
        return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
    }

    //login api
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> loginUser(@RequestBody LoginDto loginDto) {
        ApiResponse apiResponse = userService.loginUser(loginDto);
        return ResponseEntity.ok(apiResponse);
    }


}