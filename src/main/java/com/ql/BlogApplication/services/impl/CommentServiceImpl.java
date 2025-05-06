package com.ql.BlogApplication.services.impl;

import com.ql.BlogApplication.DTO.CommentDto;
import com.ql.BlogApplication.entities.Comment;
import com.ql.BlogApplication.entities.Post;
import com.ql.BlogApplication.entities.User;
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
    public CommentDto createComment(CommentDto commentDto, Long userId) {
        Comment comment = new Comment();
        comment.setComment_content(commentDto.getComment_content());
        // fetching and assigning user from user Repository
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("no user found"));
        comment.setUser(user);

        //fetch and assign post by postid from dto
        Post post = postRepository.findById(commentDto.getPost_id()).orElseThrow(() -> new ResourceNotFoundException("post not found"));
        comment.setPost(post);
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
    public CommentDto getCommentById(Long id) {
        Comment comment = commentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("comment not found which you are trying to update"));
        return mapToDto(comment);
    }

    @Override
    public CommentDto updateComment(CommentDto commentDto, Long id) {
        Comment comment = commentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("comment not found which you are trying to update"));

        comment.setComment_content(commentDto.getComment_content());

        // Don't change post or user here unless it's intentional
        Comment updatedComment = commentRepository.save(comment);
        return mapToDto(updatedComment);
    }


    @Override
    public void deleteCommentById(Long id) {
        Comment comment = commentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("comment not found which you trying to delete"));
        commentRepository.delete(comment);
    }

    //convert entity to dto
    private CommentDto mapToDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setComment_content(comment.getComment_content());
        commentDto.setPost_id(comment.getPost().getId());
        return commentDto;
    }

    // convert dto to entity
    private Comment mapToEntity(CommentDto commentDto) {
        Comment comment = new Comment();
        comment.setComment_content(commentDto.getComment_content());

        // fetch post by id
        Post post = postRepository.findById(commentDto.getPost_id()).orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        comment.setPost(post);
        return comment;

    }

}
