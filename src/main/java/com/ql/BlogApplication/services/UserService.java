package com.ql.BlogApplication.services;

import com.ql.BlogApplication.DTO.*;

import java.util.List;

public interface UserService {
    UserResponseDto createUser(UserRequestDto userRequestDto);

    List<UserResponseDto> getAllUsers();

    UserResponseDto getUserById(String id);

    UserResponseDto updateUser(UserRequestDto userRequestDto, String id);

    void deleteUser(String id);

    String loginUser(LoginDto loginDto);

    public String verifyOtp(OtpVerificationRequestDto otpVerificationRequestDto);

    public void sendOtpForLogin(OtpLoginRequestDto otpLoginRequestDto);

    String verifyLoginOtp(OtpVerificationRequestDto otpVerificationRequestDto);

}
