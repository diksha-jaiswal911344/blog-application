package com.ql.BlogApplication.repository;

import com.ql.BlogApplication.documents.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
      boolean existsByEmail(String email); // for validation before saving

}
