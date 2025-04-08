package com.ql.BlogApplication.services.impl;

import com.ql.BlogApplication.DTO.ApiResponse;
import com.ql.BlogApplication.DTO.LoginDto;
import com.ql.BlogApplication.DTO.UserRequestDto;
import com.ql.BlogApplication.DTO.UserResponseDto;
import com.ql.BlogApplication.entities.Role;
import com.ql.BlogApplication.entities.User;
import com.ql.BlogApplication.entities.UserRole;
import com.ql.BlogApplication.exceptions.ResourceNotFoundException;
import com.ql.BlogApplication.repository.RoleRepository;
import com.ql.BlogApplication.repository.UserRepository;
import com.ql.BlogApplication.repository.UserRoleRepository;
import com.ql.BlogApplication.services.UserService;
import com.ql.BlogApplication.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private JwtUtil jwtUtil;

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
        //fetch the role 1st
        Role role = roleRepository.findByName(userRequestDto.getRoleName())
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", userRequestDto.getRoleName()));
        //create and save user
        User user=mapToEntity(userRequestDto);
        user.setRole(role);
        user=userRepository.save(user);
        //fetch role by name
        Optional<Role> optionalRole=roleRepository.findByName(userRequestDto.getRoleName());
        if(optionalRole.isEmpty()){
            throw new ResourceNotFoundException("Role", "name", userRequestDto.getRoleName());
        }
        //create user role and save it
        UserRole userRole= new UserRole();
        userRole.setUser(user);
        userRole.setRole(optionalRole.get());
        userRoleRepository.save(userRole);

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

    @Override
    public UserResponseDto updateUser(UserRequestDto userRequestDto, Long id) {
        User user= userRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("User","id",id));
        user.setName(userRequestDto.getName());
        user.setEmail(userRequestDto.getEmail());
        user.setPassword(userRequestDto.getPassword());
        Optional<Role> optionalRole = roleRepository.findByName(userRequestDto.getRoleName());

        if(optionalRole.isEmpty()){
            throw new ResourceNotFoundException("optionalRole","id",id);
        }

        UserRole userRole=new UserRole();
        userRole.setUser(user);
        userRole.setRole(optionalRole.get());
        userRoleRepository.save(userRole);
        return mapToResponse(user);

    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
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
        return dto;
    }
}
