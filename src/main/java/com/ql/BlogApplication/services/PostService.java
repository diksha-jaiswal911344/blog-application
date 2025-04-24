package com.ql.BlogApplication.services;

import com.ql.BlogApplication.DTO.PostDto;
import com.ql.BlogApplication.entities.Post;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {
    PostDto createPost(PostDto postDto, MultipartFile file);

    List<PostDto> getAllPosts();

    PostDto getPostById(long id);

    PostDto updatePost(PostDto postDto, Long id, MultipartFile file);

    void deletePostById(Long id);

    List<PostDto> getPostsByCategoryId(Long categoryId);

    List<PostDto> getUncategorizedPosts();
}
