package com.ql.BlogApplication.services;

import com.ql.BlogApplication.DTO.ApiResponse;
import com.ql.BlogApplication.DTO.LoginDto;
import com.ql.BlogApplication.DTO.UserRequestDto;
import com.ql.BlogApplication.DTO.UserResponseDto;

import java.util.List;

public interface UserService {
    UserResponseDto createUser(UserRequestDto userRequestDto);
    List<UserResponseDto> getAllUsers();
    UserResponseDto getUserById(Long id);
    UserResponseDto updateUser(UserRequestDto userRequestDto, Long id);
    void deleteUser(Long id);
    ApiResponse loginUser(LoginDto loginDto);
}
