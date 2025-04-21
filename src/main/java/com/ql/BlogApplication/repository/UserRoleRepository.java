package com.ql.BlogApplication.repository;

import com.ql.BlogApplication.entities.User;
import com.ql.BlogApplication.entities.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    void deleteByUser(User user);
    List<UserRole> findByUser(User user);
}
