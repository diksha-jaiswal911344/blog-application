package com.ql.BlogApplication.services;

import com.ql.BlogApplication.DTO.PostDto;
import com.ql.BlogApplication.entities.Post;

import java.util.List;

public interface PostService {
    PostDto createPost(PostDto postDto);

    List<PostDto> getAllPosts();

    PostDto getPostById(long id);

    PostDto updatePost(PostDto postDto, Long id);

    void deletePostById(Long id);

    List<PostDto> getPostsByCategoryId(Long categoryId);

    List<PostDto> getUncategorizedPosts();
}
