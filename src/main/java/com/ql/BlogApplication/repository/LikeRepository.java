package com.ql.BlogApplication.repository;

import com.ql.BlogApplication.entities.Like;
import com.ql.BlogApplication.entities.Post;
import com.ql.BlogApplication.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like>findByUserIdAndPostId(Long userId, Long postId);
}
