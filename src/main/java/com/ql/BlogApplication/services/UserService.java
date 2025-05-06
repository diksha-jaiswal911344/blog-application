package com.ql.BlogApplication.services;

import com.ql.BlogApplication.DTO.*;

import java.util.List;
import java.util.Map;

public interface UserService {
    ApiResponseNew<Map<String, String>> createUser(UserRequestDto userRequestDto);
    // Methods to be updated
    ApiResponseNew<List<UserResponseDto>> getAllUsers();

    ApiResponseNew<UserResponseDto> getUserById(Long id);

    ApiResponseNew<UserResponseDto> updateUser(UserRequestDto userRequestDto, Long id);

    ApiResponseNew<Map<String, String>> deleteUser(Long id);

    ApiResponseNew<Map<String, String>> loginUser(LoginDto loginDto);

    ApiResponseNew<Map<String, String>> verifyOtp(OtpVerificationRequestDto otpVerificationRequestDto);

    ApiResponseNew<Map<String, String>> sendOtpForLogin(OtpLoginRequestDto otpLoginRequestDto);

    ApiResponseNew<Map<String, String>> verifyLoginOtp(OtpVerificationRequestDto dto);

}
