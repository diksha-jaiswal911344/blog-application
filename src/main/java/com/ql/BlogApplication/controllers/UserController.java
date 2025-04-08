package com.ql.BlogApplication.controllers;

import com.ql.BlogApplication.DTO.LoginDto;
import com.ql.BlogApplication.DTO.UserRequestDto;
import com.ql.BlogApplication.DTO.UserResponseDto;
import com.ql.BlogApplication.exceptions.BadRequestException;
import com.ql.BlogApplication.DTO.ApiResponse;
import com.ql.BlogApplication.services.UserService;
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

    private final UserService userService;

    @Autowired // Inject UserService
    public UserController(UserService userService) {
        this.userService = userService;
    }

    //create user
    @PostMapping(value = "/register")
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserRequestDto dto){
        return new ResponseEntity<>(userService.createUser(dto),HttpStatus.CREATED);
    }

    //read all users
    @GetMapping
    public List<UserResponseDto> getAllUsers(){
        return userService.getAllUsers();
    }

    //read by id
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id){
        return ResponseEntity.ok(userService.getUserById(id));
    }

    //update
    @PostMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(@Valid @RequestBody UserRequestDto userRequestDto, @PathVariable Long id){
        return new ResponseEntity<>(userService.updateUser(userRequestDto,id), HttpStatus.OK);
    }

    //delete
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id){
        return ResponseEntity.ok("User deleted successfully");
    }

    //login api
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> loginUser(@RequestBody LoginDto loginDto){
        ApiResponse apiResponse=userService.loginUser(loginDto);
        return ResponseEntity.ok(apiResponse);
    }
}
