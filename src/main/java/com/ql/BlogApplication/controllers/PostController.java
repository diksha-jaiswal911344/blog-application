package com.ql.BlogApplication.controllers;

import com.ql.BlogApplication.DTO.PostDto;
import com.ql.BlogApplication.services.PostService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    //CREATE A BLOG POST
    @PostMapping
    public ResponseEntity<PostDto> createPost(
            @RequestPart("post") PostDto postDto,
            @RequestParam("image") MultipartFile image) {
        return new ResponseEntity<>(postService.createPost(postDto, image), HttpStatus.CREATED);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<PostDto>> getPostsByCategory(@PathVariable(name = "categoryId") Long categoryId){
        return ResponseEntity.ok(postService.getPostsByCategoryId(categoryId));
    }

    //get all post rest apis
    @GetMapping
    public List<PostDto> getAllPosts() {
        return postService.getAllPosts();
    }

    //get post by id
    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getPostById(@PathVariable(name = "id") long id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    // update post by id
    @PutMapping("/{id}")
    public ResponseEntity<PostDto> updatePost(
            @PathVariable Long id,
            @RequestPart("post") PostDto postDto,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        return new ResponseEntity<>(postService.updatePost(postDto, id, image), HttpStatus.OK);
    }

    //delete post rest api
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePost(@PathVariable(name = "id") Long id) {
        postService.deletePostById(id);
        return new ResponseEntity<>("Post entity deleted successfully.", HttpStatus.OK);
    }

    //getting uncategorized posts
    @GetMapping("/category/uncategorized")
    public ResponseEntity<List<PostDto>>getUncategorizedPosts(){
        List<PostDto> postDtos=postService.getUncategorizedPosts();
        return ResponseEntity.ok(postDtos);
    }
}
