package com.ql.BlogApplication.controllers;

import com.ql.BlogApplication.DTO.ApiResponseNew;
import com.ql.BlogApplication.DTO.CommentDto;
//import com.ql.BlogApplication.entities.Comment;
import com.ql.BlogApplication.services.CommentService;
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

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    //create comment
    @PostMapping
    public ResponseEntity<ApiResponseNew<Map<String, CommentDto>>> createComment(@Valid @RequestBody CommentDto commentDto) {
        CommentDto createdComment = commentService.createComment(commentDto);
        Map<String,CommentDto > data= new HashMap<>();
        data.put("Object",createdComment);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseNew.success(201, data, "Comment created successfully"));
    }

    //    getAllComments
    @GetMapping
    public ResponseEntity<ApiResponseNew<Map<String, List<CommentDto>>>> getAllComments() {
        List<CommentDto> comments = commentService.getALLComment();
        Map<String,List<CommentDto> > data= new HashMap<>();
        data.put("Object", comments);
        return ResponseEntity.ok(ApiResponseNew.success(200, data, "All comments fetched successfully"));
    }

    //getCommentById
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseNew<CommentDto>> getCommentById(@PathVariable(value = "id") long id) {
        CommentDto commentDto = commentService.getCommentById(id);
        return ResponseEntity.ok(ApiResponseNew.success(200, commentDto, "Comment fetched successfully"));
    }

    //update comment
    @PutMapping("{id}")
    public ResponseEntity<ApiResponseNew<CommentDto>> updateComment(
            @Valid @RequestBody CommentDto commentDto,
            @PathVariable(value = "id") long id) {

        CommentDto updatedComment = commentService.updateComment(commentDto, id);
        return ResponseEntity.ok(ApiResponseNew.success(200, updatedComment, "Comment updated successfully"));
    }

}