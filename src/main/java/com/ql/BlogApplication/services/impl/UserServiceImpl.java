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
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;
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

    @Override
    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        if(userRepository.existsByEmail(userRequestDto.getEmail())){
            throw new RuntimeException("Email already exists");
        }
        //fetch the role
        Role role = roleRepository.findByName(userRequestDto.getRoleName())
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", userRequestDto.getRoleName()));

        //Generate OTP
        String otp = String.valueOf(new Random().nextInt(900000) + 100000); // 6-digit OTP
        Date otpGeneratedTime = new Date();

        //create and save user
        User user=mapToEntity(userRequestDto);
        user.setRole(role);
        user.setOtp(otp);
        user.setOtpGeneratedTime(otpGeneratedTime);
        user.setEmailVerified(false);
        user=userRepository.save(user);

        //create user role and save it
        UserRole userRole= new UserRole();
        userRole.setUser(user);
        userRole.setRole(role);
        userRoleRepository.save(userRole);

        //send otp email
        emailService.sendOtpEmail(user.getEmail(), otp);

        return mapToResponse(user);
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public UserResponseDto getUserById(Long id) {
        User user= userRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("User","id",id));
        return mapToResponse(user);
    }

    @Transactional
    @Override
    public UserResponseDto updateUser(UserRequestDto userRequestDto, Long id) {
        User user= userRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("User","id",id));
        user.setName(userRequestDto.getName());
        user.setEmail(userRequestDto.getEmail());
        user.setPassword(userRequestDto.getPassword());
        // save updated user
        userRepository.save(user);

        //first remove all roles to avoid duplicate roles
        userRoleRepository.deleteByUser(user);

        Role role = roleRepository.findByName(userRequestDto.getRoleName()).orElseThrow(()-> new ResourceNotFoundException("Role","name",userRequestDto.getRoleName()));

        UserRole userRole=new UserRole();
        userRole.setUser(user);
        userRole.setRole(role);
        userRoleRepository.save(userRole);

        List<UserRole> userRoles=userRoleRepository.findByUser(user);
        String updatedRoleName=userRoles.get(0).getRole().getName();

        UserResponseDto dto= new UserResponseDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRoleName(updatedRoleName);

        return dto;
    }

    @Transactional
    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        userRoleRepository.deleteByUser(user);
        userRepository.delete(user);
    }

    @Override
    public ApiResponse loginUser(LoginDto loginDto) {
        if (loginDto.getEmail() == null || loginDto.getEmail().isEmpty() ||
                loginDto.getPassword() == null || loginDto.getPassword().isEmpty()) {
            throw new RuntimeException("Email and password must not be empty");
        }

        User user = userRepository.findByEmail(loginDto.getEmail()).orElseThrow(()->new RuntimeException("User not found"));
        if(!user.getPassword().equals(loginDto.getPassword())){
            throw new RuntimeException("Invalid Credentials");
        }

        String token = jwtUtil.generateToken(user.getEmail());
        ApiResponse response = ApiResponse.builder()
                .message("Login successful")
                .success(true)
                .data(token)
                .build();
        return response;

    }

    @Override
    public String verifyOtp(OtpVerificationRequestDto otpVerificationRequestDto){
        User user = userRepository.findByEmail(otpVerificationRequestDto.getEmail()).orElseThrow(()-> new ResourceNotFoundException("User", "Email", otpVerificationRequestDto.getEmail()));

        if(user.getOtp()==null || user.getOtpGeneratedTime()==null){
            throw new RuntimeException("Otp not generated please register again");
        }

        // check otp expiry(10 min)
        long currentTime= System.currentTimeMillis();
        long otpGeneratedTime=user.getOtpGeneratedTime().getTime();

        if((currentTime - otpGeneratedTime) > 10*60*1000){
            throw new RuntimeException("otp expired please register again");
        }

        //validate otp
        if(!user.getOtp().equals(otpVerificationRequestDto.getOtp())){
            throw new RuntimeException("otp invalid");
        }

        //update user after verification
        user.setEmailVerified(true);
        user.setOtp(null);
        user.setOtpGeneratedTime(null);
        userRepository.save(user);

        return "Email varified successfully";
    }

    //send otp for login
    public void sendOtpForLogin(OtpLoginRequestDto otpLoginRequestDto) {
        User user = userRepository.findByEmail(otpLoginRequestDto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", otpLoginRequestDto.getEmail()));

        if (user.getEmailVerified()==null || !user.getEmailVerified()) {
            throw new RuntimeException("Email not verified.");
        }

        String otp = String.format("%06d",new Random().nextInt(999999));
        user.setOtp(otp);
        user.setOtpGeneratedTime(new Date());
        userRepository.save(user);

        emailService.sendOtpEmail(otpLoginRequestDto.getEmail(), otp);
    }

    @Override
    public ApiResponse verifyLoginOtp(OtpVerificationRequestDto dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", dto.getEmail()));

        if (user.getOtp() == null || !user.getOtp().equals(dto.getOtp())) {
            throw new RuntimeException("Invalid OTP.");
        }

        // Optional: Check OTP expiry (e.g., valid for 5 minutes)
        long otpAge = new Date().getTime() - user.getOtpGeneratedTime().getTime();
        if (otpAge > 5 * 60 * 1000) {
            throw new RuntimeException("OTP has expired.");
        }

        // Generate JWT
        String token = jwtUtil.generateToken(user.getEmail());

        // Clear the OTP after successful login
        user.setOtp(null);
        user.setOtpGeneratedTime(null);
        userRepository.save(user);

        return ApiResponse.builder()
                .success(true)
                .Code(200)
                .message("OTP verified successfully")
                .data(token) // or user, or whatever data
                .build();
    }

    private User mapToEntity(UserRequestDto dto){
        User user= new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        //user.setUserRoles(dto.getRoleName());
        return user;
    }

    private UserResponseDto mapToResponse (User user){
        UserResponseDto dto= new UserResponseDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRoleName(user.getRole() != null ? user.getRole().getName() : "");
        dto.setEmailVerified(user.getEmailVerified());
        return dto;
    }
}
