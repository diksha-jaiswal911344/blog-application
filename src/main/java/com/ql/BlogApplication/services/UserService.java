package com.ql.BlogApplication.services;

import com.ql.BlogApplication.DTO.UserRequestDto;
import com.ql.BlogApplication.entities.Role;
import com.ql.BlogApplication.entities.User;
import com.ql.BlogApplication.repository.RoleRepository;
import com.ql.BlogApplication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    //todo: study modelmapping
    public User createUser(UserRequestDto userRequestDto){
        Optional<User> existingUser = userRepository.findByEmail(userRequestDto.getEmail());
        if(existingUser.isPresent()){
            throw new RuntimeException("User is already present");
        }
        Role role = roleRepository.findByName(userRequestDto.getRoleName())
                .orElseThrow(()->new RuntimeException("Role not found!"));

        User newUser=User.builder()
                .name(userRequestDto.getName())
                .email(userRequestDto.getEmail())
                .password(userRequestDto.getPassword())
                .role(role)
                .build();

        User saved = userRepository.save(newUser);
        return saved;
    }

    //get user by id
    public User getUserById( Long id){
        return userRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("User not find with id:"+ id));
    }

}
