package com.ql.BlogApplication.services;

import com.ql.BlogApplication.DTO.CommentDto;

import java.util.List;

public interface CommentService {
    CommentDto createComment(CommentDto commentDto);

    List<CommentDto> getALLComment();

    CommentDto getCommentById(String id);

    CommentDto updateComment(CommentDto commentDto, String id);

    void deleteCommentById(String id);
}
