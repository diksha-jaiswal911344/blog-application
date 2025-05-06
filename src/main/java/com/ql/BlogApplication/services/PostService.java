package com.ql.BlogApplication.services;

import com.ql.BlogApplication.DTO.PostDto;
import com.ql.BlogApplication.entities.Post;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {

    PostDto createPost(PostDto postDto, Long userId);

    List<PostDto> getAllPosts();

    List<PostDto> getPostsByUserId(Long userId);

    PostDto getPostById(long id);

    PostDto updatePost(PostDto postDto, Long id, MultipartFile file, Long currentUserId);

    void deletePostById(Long id, Long currentUserId);

    List<PostDto> getPostsByCategoryId(Long categoryId);

    List<PostDto> getUncategorizedPosts();

    PostDto uploadPostImage(Long postId, MultipartFile file, Long currentUserId);
}
