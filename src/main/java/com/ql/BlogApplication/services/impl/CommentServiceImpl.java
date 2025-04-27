package com.ql.BlogApplication.services.impl;

import com.ql.BlogApplication.DTO.CommentDto;
import com.ql.BlogApplication.documents.Comment;
import com.ql.BlogApplication.documents.Post;
import com.ql.BlogApplication.documents.User;
import com.ql.BlogApplication.exceptions.ResourceNotFoundException;
import com.ql.BlogApplication.repository.CommentRepository;
import com.ql.BlogApplication.repository.PostRepository;
import com.ql.BlogApplication.repository.UserRepository;
import com.ql.BlogApplication.services.CommentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    private PostRepository postRepository;
    private UserRepository userRepository;
    private CommentRepository commentRepository;

    public CommentServiceImpl(PostRepository postRepository, UserRepository userRepository, CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public CommentDto createComment(CommentDto commentDto) {
        Comment comment = mapToEntity(commentDto);
        Comment newComment = commentRepository.save(comment);

        CommentDto commentResponse = mapToDto(newComment);
        return commentResponse;
    }

    @Override
    public List<CommentDto> getALLComment() {
        List<Comment> comments = commentRepository.findAll();
        return comments.stream().map(comment -> mapToDto(comment)).collect(Collectors.toList());
    }

    @Override
    public CommentDto getCommentById(String id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("comment", "id", id));
        return mapToDto(comment);
    }

    @Override
    public CommentDto updateComment(CommentDto commentDto, String id) {
        // First fetch the existing comment
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("comment", "id", id));

        // Update the content
        comment.setComment_content(commentDto.getComment_content());

        // Save the updated comment
        Comment updatedComment = commentRepository.save(comment);
        return mapToDto(updatedComment);
    }

    @Override
    public void deleteCommentById(String id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("comment", "id", id));
        commentRepository.delete(comment);
    }

    //convert entity to dto
    private CommentDto mapToDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setComment_content(comment.getComment_content());
        commentDto.setUser_id(comment.getUser().getId());
        commentDto.setPost_id(comment.getPost().getId());
        return commentDto;
    }

    // convert dto to entity
    private Comment mapToEntity(CommentDto commentDto) {
        Comment comment = new Comment();

        // Set ID if it exists (for updates)
        if (commentDto.getId() != null) {
            comment.setId(commentDto.getId());
        }

        comment.setComment_content(commentDto.getComment_content());

        // fetch user by id
        User user = userRepository.findById(commentDto.getUser_id())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", commentDto.getUser_id()));
        comment.setUser(user);

        // fetch post by id
        Post post = postRepository.findById(commentDto.getPost_id())
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", commentDto.getPost_id()));
        comment.setPost(post);
        return comment;
    }
}