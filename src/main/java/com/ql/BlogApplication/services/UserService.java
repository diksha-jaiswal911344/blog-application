package com.ql.BlogApplication.services;

import com.ql.BlogApplication.DTO.*;

import java.util.List;

public interface UserService {
    UserResponseDto createUser(UserRequestDto userRequestDto);

    List<UserResponseDto> getAllUsers();

    UserResponseDto getUserById(Long id);

    UserResponseDto updateUser(UserRequestDto userRequestDto, Long id);

    void deleteUser(Long id);

    ApiResponse loginUser(LoginDto loginDto);

    public String verifyOtp(OtpVerificationRequestDto otpVerificationRequestDto);

    public void sendOtpForLogin(OtpLoginRequestDto otpLoginRequestDto);

    ApiResponse verifyLoginOtp(OtpVerificationRequestDto otpVerificationRequestDto);

}
