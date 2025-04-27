package com.ql.BlogApplication.repository;

import com.ql.BlogApplication.documents.User;
import com.ql.BlogApplication.documents.UserRole;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserRoleRepository extends MongoRepository<UserRole, String> {
    void deleteByUser(User user);
    List<UserRole> findByUser(User user);
}
