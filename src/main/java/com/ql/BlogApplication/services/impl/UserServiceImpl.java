package com.ql.BlogApplication.services.impl;

import com.ql.BlogApplication.DTO.*;
import com.ql.BlogApplication.entities.Role;
import com.ql.BlogApplication.entities.User;
import com.ql.BlogApplication.entities.UserRole;
import com.ql.BlogApplication.exceptions.ResourceNotFoundException;
import com.ql.BlogApplication.repository.RoleRepository;
import com.ql.BlogApplication.repository.UserRepository;
import com.ql.BlogApplication.repository.UserRoleRepository;
import com.ql.BlogApplication.services.EmailService;
import com.ql.BlogApplication.services.UserService;
import com.ql.BlogApplication.utils.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EmailService emailService;

    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public UserServiceImpl(UserRoleRepository userRoleRepository, RoleRepository roleRepository, UserRepository userRepository) {
        this.userRoleRepository = userRoleRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    //todo: study modelmapping

    //to create user
    @Override
    public ApiResponseNew<Map<String, String>> createUser(UserRequestDto userRequestDto) {
        if (userRepository.existsByEmail(userRequestDto.getEmail())) {
            return ApiResponseNew.success(409, false, "Email already exists", Collections.emptyMap());
        }

        // Check if role exists
        Optional<Role> roleOptional = roleRepository.findByName(userRequestDto.getRoleName());
        if (roleOptional.isEmpty()) {
            return ApiResponseNew.success(404, false, "Role not found with name: " + userRequestDto.getRoleName(), Collections.emptyMap());
        }

        Role role = roleOptional.get();

        //Generate OTP
        String otp = String.valueOf(new Random().nextInt(900000) + 100000); // 6-digit OTP
        Date otpGeneratedTime = new Date();

        //create and save user
        User user = mapToEntity(userRequestDto);
        user.setRole(role);
        user.setOtp(otp);
        user.setOtpGeneratedTime(otpGeneratedTime);
        user.setEmailVerified(false);
        user = userRepository.save(user);

        //create user role and save it
        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(role);
        userRoleRepository.save(userRole);

        //send otp email
        emailService.sendOtpEmail(user.getEmail(), otp);

        Map<String, String> data = new HashMap<>();
        data.put("userId", String.valueOf(user.getId()));
        data.put("email", user.getEmail());

        return ApiResponseNew.success(201, true, "Registration successful", data);
    }

    //to get all users
    @Override
    public ApiResponseNew<List<UserResponseDto>> getAllUsers() {
        List<UserResponseDto> users = userRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ApiResponseNew.success(200, true, "Users fetched successfully", users);
    }

    //get user by id
    @Override
    public ApiResponseNew<UserResponseDto> getUserById(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            return ApiResponseNew.success(404, false, "User not found with id: " + id, null);
        }

        UserResponseDto userResponseDto = mapToResponse(userOptional.get());
        return ApiResponseNew.success(200, true, "User fetched successfully", userResponseDto);
    }

    //to update user
    @Transactional
    @Override
    public ApiResponseNew<UserResponseDto> updateUser(UserRequestDto userRequestDto, Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            return ApiResponseNew.success(404, false, "User not found with id: " + id, null);
        }

        User user = userOptional.get();

        // Check if role exists
        Optional<Role> roleOptional = roleRepository.findByName(userRequestDto.getRoleName());
        if (roleOptional.isEmpty()) {
            return ApiResponseNew.success(404, false, "Role not found with name: " + userRequestDto.getRoleName(), null);
        }

        Role role = roleOptional.get();

        user.setName(userRequestDto.getName());
        user.setEmail(userRequestDto.getEmail());
        user.setPassword(userRequestDto.getPassword());
        // save updated user
        userRepository.save(user);

        //first remove all roles to avoid duplicate roles
        userRoleRepository.deleteByUser(user);

        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(role);
        userRoleRepository.save(userRole);

        List<UserRole> userRoles = userRoleRepository.findByUser(user);
        String updatedRoleName = userRoles.isEmpty() ? "" : userRoles.get(0).getRole().getName();

        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRoleName(updatedRoleName);
        dto.setEmailVerified(user.getEmailVerified());

        return ApiResponseNew.success(200, true, "User updated successfully", dto);
    }

    // to delete user
    @Transactional
    @Override
    public ApiResponseNew<Map<String, String>> deleteUser(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            return ApiResponseNew.success(404, false, "User not found with id: " + id, Collections.emptyMap());
        }

        User user = userOptional.get();
        userRoleRepository.deleteByUser(user);
        userRepository.delete(user);

        Map<String, String> data = new HashMap<>();
        data.put("status", "deleted");
        data.put("userId", id.toString());

        return ApiResponseNew.success(200, true, "User deleted successfully", data);
    }

    @Override
    public ApiResponseNew<Map<String, String>> loginUser(LoginDto loginDto) {
        if (loginDto.getEmail() == null || loginDto.getEmail().isEmpty() ||
                loginDto.getPassword() == null || loginDto.getPassword().isEmpty()) {
            return ApiResponseNew.success(400, false, "Email and password must not be empty", Collections.emptyMap());
        }

        Optional<User> userOptional = userRepository.findByEmail(loginDto.getEmail());
        if (userOptional.isEmpty()) {
            return ApiResponseNew.success(404, false, "User not found with email: " + loginDto.getEmail(), Collections.emptyMap());
        }

        User user = userOptional.get();

        if (!user.getPassword().equals(loginDto.getPassword())) {
            return ApiResponseNew.success(401, false, "Invalid credentials", Collections.emptyMap());
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getId());

        Map<String, String> data = new HashMap<>();
        data.put("token", token);
        data.put("userId", user.getId().toString());

        return ApiResponseNew.success(200, true, "Login successful", data);
    }

    //to varify otp
    @Override
    public ApiResponseNew<Map<String, String>> verifyOtp(OtpVerificationRequestDto otpVerificationRequestDto) {
        Optional<User> userOptional = userRepository.findByEmail(otpVerificationRequestDto.getEmail());
        if (userOptional.isEmpty()) {
            return ApiResponseNew.success(404, false, "User not found with email: " + otpVerificationRequestDto.getEmail(), Collections.emptyMap());
        }

        User user = userOptional.get();

        if (user.getOtp() == null || user.getOtpGeneratedTime() == null) {
            return ApiResponseNew.success(400, false, "OTP not generated. Please register again", Collections.emptyMap());
        }

        // check otp expiry(10 min)
        long currentTime = System.currentTimeMillis();
        long otpGeneratedTime = user.getOtpGeneratedTime().getTime();

        if ((currentTime - otpGeneratedTime) > 10 * 60 * 1000) {
            return ApiResponseNew.success(400, false, "OTP expired. Please register again", Collections.emptyMap());
        }

        //validate otp
        if (!user.getOtp().equals(otpVerificationRequestDto.getOtp())) {
            return ApiResponseNew.success(400, false, "OTP invalid", Collections.emptyMap());
        }

        //update user after verification
        user.setEmailVerified(true);
        user.setOtp(null);
        user.setOtpGeneratedTime(null);
        userRepository.save(user);

        Map<String, String> data = new HashMap<>();
        data.put("message", "Email verified successfully");
        data.put("userId", user.getId().toString());

        return ApiResponseNew.success(200, true, "OTP verification successful", data);
    }

    //to sendOtpForLogin
    @Override
    public ApiResponseNew<Map<String, String>> sendOtpForLogin(OtpLoginRequestDto otpLoginRequestDto) {
        Optional<User> userOptional = userRepository.findByEmail(otpLoginRequestDto.getEmail());
        if (userOptional.isEmpty()) {
            return ApiResponseNew.success(404, false, "User not found with email: " + otpLoginRequestDto.getEmail(), Collections.emptyMap());
        }

        User user = userOptional.get();

        if (user.getEmailVerified() == null || !user.getEmailVerified()) {
            return ApiResponseNew.success(400, false, "Email not verified", Collections.emptyMap());
        }

        String otp = String.format("%06d", new Random().nextInt(999999));
        user.setOtp(otp);
        user.setOtpGeneratedTime(new Date());
        userRepository.save(user);

        try {
            emailService.sendOtpEmail(otpLoginRequestDto.getEmail(), otp);
        } catch (Exception e) {
            // Log email sending error but continue
            return ApiResponseNew.success(500, false, "Failed to send OTP email", Collections.emptyMap());
        }

        Map<String, String> data = new HashMap<>();
        data.put("status", "sent");
        data.put("email", user.getEmail());

        return ApiResponseNew.success(200, true, "OTP sent to the registered email", data);
    }

    //to verifyLoginOtp
    @Override
    public ApiResponseNew<Map<String, String>> verifyLoginOtp(OtpVerificationRequestDto dto) {
        Optional<User> userOptional = userRepository.findByEmail(dto.getEmail());
        if (userOptional.isEmpty()) {
            return ApiResponseNew.success(404, false, "User not found with email: " + dto.getEmail(), Collections.emptyMap());
        }

        User user = userOptional.get();

        if (user.getOtp() == null || !user.getOtp().equals(dto.getOtp())) {
            return ApiResponseNew.success(400, false, "Invalid OTP", Collections.emptyMap());
        }

        // Check OTP expiry (5 minutes)
        long otpAge = new Date().getTime() - user.getOtpGeneratedTime().getTime();
        if (otpAge > 5 * 60 * 1000) {
            return ApiResponseNew.success(400, false, "OTP has expired", Collections.emptyMap());
        }

        // Generate JWT
        String token = jwtUtil.generateToken(user.getEmail(), user.getId());

        // Clear the OTP after successful login
        user.setOtp(null);
        user.setOtpGeneratedTime(null);
        userRepository.save(user);

        Map<String, String> data = new HashMap<>();
        data.put("token", token);
        data.put("userId", user.getId().toString());

        return ApiResponseNew.success(200, true, "Login OTP verified", data);
    }

    private User mapToEntity(UserRequestDto dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        return user;
    }

    private UserResponseDto mapToResponse(User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRoleName(user.getRole() != null ? user.getRole().getName() : "");
        dto.setEmailVerified(user.getEmailVerified());
        return dto;
    }
}
