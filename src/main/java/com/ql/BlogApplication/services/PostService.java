package com.ql.BlogApplication.services;

import com.ql.BlogApplication.DTO.PostDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {
//    PostDto createPost(PostDto postDto, MultipartFile file);
      PostDto createPost(PostDto postDto);


    List<PostDto> getAllPosts();

    PostDto getPostById(String id);

    PostDto updatePost(PostDto postDto, String id, MultipartFile file);

    void deletePostById(String id);

    List<PostDto> getPostsByCategoryId(String categoryId);

    List<PostDto> getUncategorizedPosts();

    PostDto uploadPostImage(String postId, MultipartFile file);
}
