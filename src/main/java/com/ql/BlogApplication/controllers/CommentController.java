package com.ql.BlogApplication.controllers;

import com.ql.BlogApplication.DTO.ApiResponseNew;
import com.ql.BlogApplication.DTO.CommentDto;
//import com.ql.BlogApplication.entities.Comment;
import com.ql.BlogApplication.entities.User;
import com.ql.BlogApplication.repository.UserRepository;
import com.ql.BlogApplication.services.CommentService;
import com.ql.BlogApplication.utils.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
    private CommentService commentService;

    private final JwtUtil jwtUtil;

    private final UserRepository userRepository;

    public CommentController(CommentService commentService, JwtUtil jwtUtil, UserRepository userRepository) {
        this.commentService = commentService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    //create comment
    @PostMapping
    public ResponseEntity<ApiResponseNew<Map<String, CommentDto>>> createComment(@Valid @RequestBody CommentDto commentDto, @RequestHeader("Authorization") String token) {
        String email=jwtUtil.extractEmail(token.substring(7));
        User user= userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("LoggedIn user not found"));

        CommentDto createdComment = commentService.createComment(commentDto, user.getId());
        Map<String,CommentDto > data= new HashMap<>();
        data.put("Object",createdComment);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseNew.success(201, true, "Comment created successfully",data));
    }

    //    getAllComments
    @GetMapping
    public ResponseEntity<ApiResponseNew<Map<String, List<CommentDto>>>> getAllComments() {
        List<CommentDto> comments = commentService.getALLComment();
        Map<String,List<CommentDto> > data= new HashMap<>();
        data.put("Object", comments);
        return ResponseEntity.ok(ApiResponseNew.success(200, true, "All comments fetched successfully",data));
    }

    //getCommentById
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseNew<CommentDto>> getCommentById(@PathVariable(value = "id") long id) {
        CommentDto commentDto = commentService.getCommentById(id);
        return ResponseEntity.ok(ApiResponseNew.success(200,true, "Comment fetched successfully", commentDto));
    }

    //update comment
    @PutMapping
    public ResponseEntity<ApiResponseNew<CommentDto>> updateComment(
            @Valid @RequestBody CommentDto commentDto,
            @RequestHeader("Authorization") String token) {
        String email=jwtUtil.extractEmail(token.substring(7));
        User user= userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("LoggedIn user not found"));
        CommentDto updatedComment = commentService.updateComment(commentDto, user.getId());
        return ResponseEntity.ok(ApiResponseNew.success(200, true, "Comment updated successfully",updatedComment));
    }

}