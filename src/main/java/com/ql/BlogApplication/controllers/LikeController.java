package com.ql.BlogApplication.controllers;

import com.ql.BlogApplication.DTO.ApiResponseNew;
import com.ql.BlogApplication.DTO.LikeDto;
import com.ql.BlogApplication.entities.User;
import com.ql.BlogApplication.repository.UserRepository;
import com.ql.BlogApplication.services.LikeService;
import com.ql.BlogApplication.utils.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/likes")
public class LikeController {
    private LikeService likeService;

    private final JwtUtil jwtUtil;

    private final UserRepository userRepository;

    public LikeController(LikeService likeService, JwtUtil jwtUtil, UserRepository userRepository) {
        this.likeService = likeService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<ApiResponseNew<Map<String, String>>> toggleLike(@RequestBody @Valid LikeDto likeDto, @RequestHeader("Authorization") String token) {

        String email= jwtUtil.extractEmail(token.substring(7));
        User user= userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("loggedIn User not found"));

        String response = likeService.toggleLike(likeDto, user.getId());
        Map<String,String> data= new HashMap<>();
        data.put("message",response);
        return ResponseEntity.ok(
                ApiResponseNew.success(200, data, "Like status updated successfully")
        );

    }
}
