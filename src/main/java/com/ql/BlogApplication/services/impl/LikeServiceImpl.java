package com.ql.BlogApplication.services.impl;

import com.ql.BlogApplication.DTO.LikeDto;
import com.ql.BlogApplication.entities.Like;
import com.ql.BlogApplication.entities.Post;
import com.ql.BlogApplication.entities.User;
import com.ql.BlogApplication.entities.UserRole;
import com.ql.BlogApplication.exceptions.ResourceNotFoundException;
import com.ql.BlogApplication.repository.LikeRepository;
import com.ql.BlogApplication.repository.PostRepository;
import com.ql.BlogApplication.repository.UserRepository;
import com.ql.BlogApplication.services.LikeService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LikeServiceImpl implements LikeService {

    private static final Logger logger = LoggerFactory.getLogger(LikeServiceImpl.class);

    private UserRepository userRepository;
    private LikeRepository likeRepository;
    private PostRepository postRepository;

    public LikeServiceImpl(UserRepository userRepository, LikeRepository likeRepository, PostRepository postRepository) {
        this.userRepository = userRepository;
        this.likeRepository = likeRepository;
        this.postRepository = postRepository;
    }

    @Transactional
    @Override
    public String toggleLike(LikeDto likeDto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("user not found with given id"));
        Post post = postRepository.findById(likeDto.getPostId()).orElseThrow(() -> new ResourceNotFoundException("post not found"));

        logger.info("request is arived:{}", likeDto.isLike());
        Optional<Like> existingLikeOpt = likeRepository.findLikesByUserIdAndPostId(userId, likeDto.getPostId());

        if (likeDto.isLike()) {
            //user wants to like

            if (existingLikeOpt.isPresent()) {
                return "already liked";
            }
            logger.info("creating like");
            Like like = new Like();
            like.setUser(user);
            like.setPost(post);
            likeRepository.save(like);
            return "Post Liked Successfully";
        } else {

            //user want's to unlike
            if (existingLikeOpt.isPresent()) {
                likeRepository.delete(existingLikeOpt.get());
                return "post unliked successfully";
            } else {
                throw new ResourceNotFoundException("user id or post id not found");
            }
        }
    }
}
