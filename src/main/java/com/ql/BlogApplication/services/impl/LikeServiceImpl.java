package com.ql.BlogApplication.services.impl;

import com.ql.BlogApplication.DTO.LikeDto;
import com.ql.BlogApplication.documents.Like;
import com.ql.BlogApplication.documents.Post;
import com.ql.BlogApplication.documents.User;
import com.ql.BlogApplication.exceptions.ResourceNotFoundException;
import com.ql.BlogApplication.repository.LikeRepository;
import com.ql.BlogApplication.repository.PostRepository;
import com.ql.BlogApplication.repository.UserRepository;
import com.ql.BlogApplication.services.LikeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LikeServiceImpl implements LikeService {

    private static final Logger logger = LoggerFactory.getLogger(LikeServiceImpl.class);

    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;

    public LikeServiceImpl(UserRepository userRepository, LikeRepository likeRepository, PostRepository postRepository) {
        this.userRepository = userRepository;
        this.likeRepository = likeRepository;
        this.postRepository = postRepository;
    }

    @Override
    public String toggleLike(LikeDto likeDto) {
        // Fetch user and post documents
        User user = userRepository.findById(likeDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("user", "id", likeDto.getUserId()));
        Post post = postRepository.findById(likeDto.getPostId())
                .orElseThrow(() -> new ResourceNotFoundException("post", "id", likeDto.getPostId()));

        logger.info("Request received: userId={}, postId={}, like={}",
                likeDto.getUserId(), likeDto.getPostId(), likeDto.isLike());

        Optional<Like> existingLikeOpt = likeRepository.findByUserIdAndPostId(
                likeDto.getUserId(), likeDto.getPostId());

        if (likeDto.isLike()) {
            // User wants to like
            if (existingLikeOpt.isPresent()) {
                return "Already liked";
            }

            logger.info("Creating new like");
            Like like = new Like();
            like.setUser(user);
            like.setPost(post);
            likeRepository.save(like);
            return "Post liked successfully";
        } else {
            // User wants to unlike
            if (existingLikeOpt.isPresent()) {
                likeRepository.delete(existingLikeOpt.get());
                return "Post unliked successfully";
            } else {
                throw new ResourceNotFoundException("Like", "userId & postId",
                        likeDto.getUserId() + " & " + likeDto.getPostId());
            }
        }
    }
}