package com.ql.BlogApplication.repository;

import com.ql.BlogApplication.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    //
}
