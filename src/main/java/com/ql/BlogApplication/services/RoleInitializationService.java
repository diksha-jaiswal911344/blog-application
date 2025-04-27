package com.ql.BlogApplication.services;


import com.ql.BlogApplication.documents.Role;
import com.ql.BlogApplication.repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class RoleInitializationService {

    @Autowired
    private RoleRepository roleRepository;

    @PostConstruct
    public void initRoles() {
        // List of required roles
        List<String> requiredRoles = Arrays.asList("author", "viewer");

        for (String roleName : requiredRoles) {
            // Check if role exists, if not create it
            if (!roleRepository.existsByName(roleName)) {
                Role role = new Role();
                role.setName(roleName);
                roleRepository.save(role);
            }
        }
    }
}
