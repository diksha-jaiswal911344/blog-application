package com.ql.BlogApplication.repository;

import com.ql.BlogApplication.documents.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CommentRepository extends MongoRepository<Comment, String> {
    // You might want to add methods to find comments by post or user
    List<Comment> findByPostId(String postId);
    List<Comment> findByUserId(String userId);
}
