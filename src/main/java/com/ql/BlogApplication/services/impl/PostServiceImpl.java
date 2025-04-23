package com.ql.BlogApplication.services.impl;

import com.ql.BlogApplication.DTO.PostDto;
import com.ql.BlogApplication.entities.Category;
import com.ql.BlogApplication.entities.Post;
import com.ql.BlogApplication.exceptions.ResourceNotFoundException;
import com.ql.BlogApplication.repository.CategoryRepository;
import com.ql.BlogApplication.repository.PostRepository;
import com.ql.BlogApplication.services.PostService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    private PostRepository postRepository;
    private final CategoryRepository categoryRepository;

    public PostServiceImpl(PostRepository postRepository, CategoryRepository categoryRepository) {
        this.postRepository = postRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public PostDto createPost(PostDto postDto) {
       // convt dto to entity
        Post post=mapToEntity(postDto);
        Category category=categoryRepository.findById(postDto.getCategoryId()).orElseThrow(()->new ResourceNotFoundException("category","id",postDto.getCategoryId()));
        post.setCategory(category);
        Post newPost=postRepository.save(post);

        //convt entity to dto
        PostDto postResponse= mapToDTO(newPost);
        return postResponse;
    }



    @Override
    public List<PostDto> getAllPosts() {
        List<Post>posts=postRepository.findAll();
        return posts.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
    }

    @Override
    public PostDto getPostById(long id) {
         Post post= postRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Post","id",id));
        return mapToDTO(post);
    }

    @Override
    public PostDto updatePost(PostDto postDto, Long id) {
        // get post by id from the db
        Post post= postRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Post","id",id));
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        post.setPublished(postDto.isPublished());
        Post updatedPost= postRepository.save(post);
        return mapToDTO(updatedPost);
    }

    @Override
    public void deletePostById(Long id) {
        Post post= postRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Post","id",id));
        postRepository.delete(post);
    }

    @Override
    public List<PostDto> getPostsByCategoryId(Long categoryId) {
        List<Post> posts=postRepository.findByCategoryId(categoryId);
        return posts.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public List<PostDto> getUncategorizedPosts() {
        Category uncategorized=categoryRepository.findByCategoryName("Uncategorized").orElseThrow(()->new RuntimeException("uncategorized not found"));

        List<Post> posts=postRepository.findByCategory(uncategorized);

        return posts.stream().map(this::mapToDTO).collect(Collectors.toList());
    }


    //convt enity into dto
    private  PostDto mapToDTO(Post post){
        PostDto postDto = new PostDto();
        postDto.setId(post.getId());
        postDto.setTitle(post.getTitle());
        postDto.setContent(post.getContent());
        postDto.setPublished(post.isPublished());
        postDto.setCategoryId(post.getCategory().getId());
        return postDto;
    }
    //convt dto to entity
    private Post mapToEntity(PostDto postDto){
        Post post= new Post();
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        post.setPublished(postDto.isPublished());
//
//        Category category = categoryRepository.findById(postDto.getCategoryId())
//                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", postDto.getCategoryId()));
//        post.setCategory(category);

        return post;
    }

}
