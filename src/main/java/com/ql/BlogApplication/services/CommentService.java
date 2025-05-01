package com.ql.BlogApplication.services;

import com.ql.BlogApplication.DTO.CategoryDto;
import com.ql.BlogApplication.DTO.CommentDto;
import com.ql.BlogApplication.entities.Comment;

import java.util.List;

public interface CommentService {
    CommentDto createComment(CommentDto commentDto, Long userId);

    List<CommentDto> getALLComment();

    CommentDto getCommentById(Long id);

    CommentDto updateComment(CommentDto commentDto, Long id);

    void deleteCommentById(Long id);
}
