//package com.ql.BlogApplication.controllers;
//
//import com.ql.BlogApplication.DTO.ApiResponseNew;
//import com.ql.BlogApplication.DTO.PostDto;
//import com.ql.BlogApplication.services.PostService;
//import jakarta.validation.Valid;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/posts")
//public class PostController {
//    private PostService postService;
//
//    public PostController(PostService postService) {
//        this.postService = postService;
//    }
//
//    @PostMapping
//    public ResponseEntity<ApiResponseNew<Map<String, PostDto>>> createPostTextOnly(
//            @Valid @RequestBody PostDto postDto) {
//        PostDto created = postService.createPost(postDto);
//        Map<String,PostDto> data=new HashMap<>();
//        data.put("Object",created);
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .body(ApiResponseNew.success(201, data, "Post created successfully"));
//    }
//
//
//    // 2) Image upload for existing post
//    @PostMapping("/{postId}/image")
//    public ResponseEntity<ApiResponseNew<PostDto>> uploadImage(
//            @PathVariable Long postId,
//            @RequestParam("image") MultipartFile image) {
//
//        PostDto updated = postService.uploadPostImage(postId, image);
//        Map<String, PostDto> data= new HashMap<>();
//        data.put("Object",updated);
//
//        return ResponseEntity.ok(
//                ApiResponseNew.success(200, updated, "Image uploaded and post updated successfully")
//        );
//    }
//
//    @GetMapping("/category/{categoryId}")
//    public ResponseEntity<ApiResponseNew<Map<String, List<PostDto>>>> getPostsByCategory(
//            @PathVariable(name = "categoryId") Long categoryId) {
//
//        List<PostDto> posts = postService.getPostsByCategoryId(categoryId);
//        Map<String, List<PostDto>> data=new HashMap<>();
//        data.put("AvailablePosts",posts);
//        return ResponseEntity.ok(
//                ApiResponseNew.success(200, data, "Posts fetched successfully for the category")
//        );
//    }
//
//
//    //get all post rest apis
//    @GetMapping
//    public ResponseEntity<ApiResponseNew<Map<String, List<PostDto>>>> getAllPosts() {
//        List<PostDto> posts = postService.getAllPosts();
//        Map<String,List<PostDto>> data= new HashMap<>();
//        data.put("Object",posts);
//        return ResponseEntity.ok(
//                ApiResponseNew.success(200, data, "All posts fetched successfully")
//        );
//    }
//
//    //get post by id
//    @GetMapping("/{id}")
//    public ResponseEntity<ApiResponseNew<PostDto>> getPostById(@PathVariable(name = "id") long id) {
//        PostDto post = postService.getPostById(id);
//        return ResponseEntity.ok(
//                ApiResponseNew.success(200, post, "Post fetched successfully")
//        );
//    }
//
//    // update post by id
//    @PutMapping("/{id}")
//    public ResponseEntity<ApiResponseNew<PostDto>> updatePost(
//            @PathVariable Long id,
//            @RequestPart("post") PostDto postDto,
//            @RequestParam(value = "image", required = false) MultipartFile image) {
//
//        PostDto updatedPost = postService.updatePost(postDto, id, image);
//        return ResponseEntity.ok(
//                ApiResponseNew.success(200, updatedPost, "Post updated successfully")
//        );
//    }
//
//    //delete post rest api
//    @DeleteMapping("/{id}")
//    public ResponseEntity<ApiResponseNew<String>> deletePost(@PathVariable(name = "id") Long id) {
//        postService.deletePostById(id);
//        return ResponseEntity.ok(
//                ApiResponseNew.success(200, "Post entity deleted successfully.", "Deleted")
//        );
//    }
//
//    //getting uncategorized posts
//    @GetMapping("/category/uncategorized")
//    public ResponseEntity<ApiResponseNew<Map<String, List<PostDto>>>> getUncategorizedPosts() {
//        List<PostDto> postDtos = postService.getUncategorizedPosts();
//        Map<String, List<PostDto>> data=new HashMap<>();
//        data.put("Object",postDtos);
//        return ResponseEntity.ok(
//                ApiResponseNew.success(200, data, "Uncategorized posts fetched successfully")
//        );
//    }
//
//}

package com.ql.BlogApplication.controllers;

import com.ql.BlogApplication.DTO.ApiResponseNew;
import com.ql.BlogApplication.DTO.PostDto;
import com.ql.BlogApplication.services.PostService;
import com.ql.BlogApplication.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
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
    private final PostService postService;
    private final JwtUtil jwtUtil;

    public PostController(PostService postService, JwtUtil jwtUtil) {
        this.postService = postService;
        this.jwtUtil = jwtUtil;
    }

    // Helper method to extract the current user ID from JWT token
    private Long getCurrentUserId(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return jwtUtil.getUserIdFromToken(token);
        }
        return null; // Handle this appropriately in your application
    }

    @PostMapping
    public ResponseEntity<ApiResponseNew<Map<String, PostDto>>> createPost(@Valid @RequestBody PostDto postDto, HttpServletRequest request) {

        // Extract user ID from JWT token - the client doesn't need to provide this
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            Map<String, PostDto> errorData = new HashMap<>();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponseNew.success(401, false, "unauthorized access", errorData));
        }

        // Even if userId is in the postDto, we ignore it and use the one from the token
        PostDto created = postService.createPost(postDto, userId);
        Map<String, PostDto> data = new HashMap<>();
        data.put("Object", created);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseNew.success(201, true, "Post created successfully", data));
    }

    // Image upload for existing post
    @PostMapping("/{postId}/image")
    public ResponseEntity<ApiResponseNew<PostDto>> uploadImage(@PathVariable Long postId, @RequestParam("image") MultipartFile image, HttpServletRequest request) {

        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponseNew.success(401, false, "Unauthorized access", null));
        }

        PostDto updated = postService.uploadPostImage(postId, image, userId);

        return ResponseEntity.ok(ApiResponseNew.success(200, true, "Image uploaded and post updated successfully", updated));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponseNew<Map<String, List<PostDto>>>> getPostsByCategory(@PathVariable(name = "categoryId") Long categoryId) {

        List<PostDto> posts = postService.getPostsByCategoryId(categoryId);
        Map<String, List<PostDto>> data = new HashMap<>();
        data.put("AvailablePosts", posts);

        return ResponseEntity.ok(ApiResponseNew.success(200, true, "Posts fetched successfully for the category", data));
    }

    // Get all posts
    @GetMapping
    public ResponseEntity<ApiResponseNew<Map<String, List<PostDto>>>> getAllPosts() {
        List<PostDto> posts = postService.getAllPosts();
        Map<String, List<PostDto>> data = new HashMap<>();
        data.put("Object", posts);

        return ResponseEntity.ok(ApiResponseNew.success(200, true, "All posts fetched successfully", data));
    }

    // Get post by id
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseNew<PostDto>> getPostById(@PathVariable(name = "id") long id) {
        PostDto post = postService.getPostById(id);

        return ResponseEntity.ok(ApiResponseNew.success(200, true, "Post fetched successfully", post));
    }

    // Update post by id
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseNew<PostDto>> updatePost(@PathVariable Long id, @RequestPart("post") PostDto postDto, @RequestParam(value = "image", required = false) MultipartFile image, HttpServletRequest request) {

        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponseNew.success(401, false, "unauthorized access", null));
        }

        PostDto updatedPost = postService.updatePost(postDto, id, image, userId);

        return ResponseEntity.ok(ApiResponseNew.success(200, true, "Post updated successfully", updatedPost));
    }

    // Delete post rest api
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseNew<String>> deletePost(@PathVariable(name = "id") Long id, HttpServletRequest request) {

        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponseNew.success(401, false, "Unauthorized access", null));
        }

        postService.deletePostById(id, userId);

        return ResponseEntity.ok(ApiResponseNew.success(200, true, "Post entity deleted successfully.", null));
    }

    // Getting uncategorized posts
    @GetMapping("/category/uncategorized")
    public ResponseEntity<ApiResponseNew<Map<String, List<PostDto>>>> getUncategorizedPosts() {
        List<PostDto> postDtos = postService.getUncategorizedPosts();
        Map<String, List<PostDto>> data = new HashMap<>();
        data.put("Object", postDtos);

        return ResponseEntity.ok(ApiResponseNew.success(200, true, "Uncategorized posts fetched successfully", data));
    }

    // Get posts created by current user
    @GetMapping("/my-posts")
    public ResponseEntity<ApiResponseNew<Map<String, List<PostDto>>>> getMyPosts(HttpServletRequest request) {

        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponseNew.success(401, false, "Unauthorized access", null));
        }

        List<PostDto> posts = postService.getPostsByUserId(userId);
        Map<String, List<PostDto>> data = new HashMap<>();
        data.put("Object", posts);

        return ResponseEntity.ok(ApiResponseNew.success(200, true, "User's posts fetched successfully", data));
    }
}