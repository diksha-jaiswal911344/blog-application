package com.ql.BlogApplication.repository;

import com.ql.BlogApplication.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>{
    Optional<User> findByEmail(String email);
      boolean existsByEmail(String email); // for validation before saving

}
