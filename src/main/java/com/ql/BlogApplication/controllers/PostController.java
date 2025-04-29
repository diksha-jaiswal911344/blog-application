package com.ql.BlogApplication.controllers;

import com.ql.BlogApplication.DTO.ApiResponseNew;
import com.ql.BlogApplication.DTO.PostDto;
import com.ql.BlogApplication.services.PostService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<ApiResponseNew<Map<String, PostDto>>> createPostTextOnly(
            @Valid @RequestBody PostDto postDto) {
        PostDto created = postService.createPost(postDto);
        Map<String,PostDto> data = new HashMap<>();
        data.put("Object", created);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseNew.success(201, data, "Post created successfully"));
    }

    // 2) Image upload for existing post
    @PostMapping("/{postTitle}/image")
    public ResponseEntity<ApiResponseNew<Map<String, PostDto>>> uploadImage(
            @PathVariable String postTitle,
            @RequestParam(value = "image", required = true) MultipartFile image) {

        PostDto updated = postService.uploadPostImage(postTitle, image);
        Map<String, PostDto> data = new HashMap<>();
        data.put("Object", updated);

        return ResponseEntity.ok(
                ApiResponseNew.success(200, data, "Image uploaded and post updated successfully")
        );
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponseNew<Map<String, List<PostDto>>>> getPostsByCategory(
            @PathVariable(name = "categoryId") String categoryId) {

        List<PostDto> posts = postService.getPostsByCategoryId(categoryId);
        Map<String, List<PostDto>> data = new HashMap<>();
        data.put("AvailablePosts", posts);
        return ResponseEntity.ok(
                ApiResponseNew.success(200, data, "Posts fetched successfully for the category")
        );
    }

    // Get all posts rest api
    @GetMapping
    public ResponseEntity<ApiResponseNew<Map<String, List<PostDto>>>> getAllPosts() {
        List<PostDto> posts = postService.getAllPosts();
        Map<String, List<PostDto>> data = new HashMap<>();
        data.put("Object", posts);
        return ResponseEntity.ok(
                ApiResponseNew.success(200, data, "All posts fetched successfully")
        );
    }

    // Get post by id
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseNew<PostDto>> getPostById(@PathVariable(name = "id") String id) {
        PostDto post = postService.getPostById(id);
        return ResponseEntity.ok(
                ApiResponseNew.success(200, post, "Post fetched successfully")
        );
    }

    // Update post by id
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseNew<PostDto>> updatePost(
            @PathVariable String id,
            @RequestPart("post") PostDto postDto,
            @RequestParam(value = "image", required = false) MultipartFile image) {

        PostDto updatedPost = postService.updatePost(postDto, id, image);
        return ResponseEntity.ok(
                ApiResponseNew.success(200, updatedPost, "Post updated successfully")
        );
    }

    // Delete post rest api
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseNew<String>> deletePost(@PathVariable(name = "id") String id) {
        postService.deletePostById(id);
        return ResponseEntity.ok(
                ApiResponseNew.success(200, "Post entity deleted successfully.", "Deleted")
        );
    }

    // Getting uncategorized posts
    @GetMapping("/category/uncategorized")
    public ResponseEntity<ApiResponseNew<Map<String, List<PostDto>>>> getUncategorizedPosts() {
        List<PostDto> postDtos = postService.getUncategorizedPosts();
        Map<String, List<PostDto>> data = new HashMap<>();
        data.put("Object", postDtos);
        return ResponseEntity.ok(
                ApiResponseNew.success(200, data, "Uncategorized posts fetched successfully")
        );
    }
}