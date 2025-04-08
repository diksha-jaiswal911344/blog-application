package com.ql.BlogApplication.repository;

import com.ql.BlogApplication.entities.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    //
}
