package com.ql.BlogApplication.controllers;

import com.ql.BlogApplication.DTO.CommentDto;
import com.ql.BlogApplication.entities.Comment;
import com.ql.BlogApplication.services.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
    private CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    //create comment
    @PostMapping
    public ResponseEntity<CommentDto> createComment(@Valid @RequestBody CommentDto commentDto){
        return new ResponseEntity<>(commentService.createComment(commentDto), HttpStatus.CREATED);
    }

//    getAllComments
    @GetMapping
    public List<CommentDto> getAllComments(){
        return commentService.getALLComment();
    }

    //getCommentById
    @GetMapping("/{id}")
    public ResponseEntity<CommentDto> getCommentById (@PathVariable(value = "id") long id){
        return ResponseEntity.ok(commentService.getCommentById(id));
    }

    //update comment
    @PutMapping("{id}")
    public ResponseEntity<CommentDto> updateComment(@Valid @RequestBody CommentDto commentDto, @PathVariable(value = "id")long id){
        CommentDto updatedComment=commentService.updateComment(commentDto,id);
        return new ResponseEntity<>(updatedComment,HttpStatus.OK);
    }

}