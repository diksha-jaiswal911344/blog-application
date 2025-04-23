package com.ql.BlogApplication.services.impl;

import com.ql.BlogApplication.DTO.CategoryDto;
import com.ql.BlogApplication.entities.Category;
import com.ql.BlogApplication.entities.Post;
import com.ql.BlogApplication.exceptions.ResourceNotFoundException;
import com.ql.BlogApplication.repository.CategoryRepository;
import com.ql.BlogApplication.repository.PostRepository;
import com.ql.BlogApplication.services.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private CategoryRepository categoryRepository;

    private PostRepository postRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository, PostRepository postRepository) {
        this.categoryRepository = categoryRepository;
        this.postRepository = postRepository;
    }

    @Override
    public CategoryDto createCategory(CategoryDto categoryDto) {
        //we have to save to db
        Category category=mapToEntity(categoryDto);
        Category newCategory= categoryRepository.save(category);

        // we have to give respose to the client
        CategoryDto categoryResponse=mapToDto(newCategory);
        return categoryResponse;
    }

    @Override
    public List<CategoryDto> getALLCategory() {
        List<Category> categories= categoryRepository.findAll();
        return categories.stream().map(category -> mapToDto(category)).collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryById(Long id) {
        Category category=categoryRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("category","id",id));
        return mapToDto(category);
    }

    @Override
    public CategoryDto updateCategory(CategoryDto categoryDto, Long id) {
        Category category=categoryRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("category","id",id));
        category.setTitle(categoryDto.getTitle());
        category.setCategoryName(categoryDto.getCategory_name());
        Category updatedCategory=categoryRepository.save(category);
        return mapToDto(updatedCategory);
    }

    @Override
    public void deleteCategory(Long categoryId) {
        //checking whether category is there in db or not
        Category category=categoryRepository.findById(categoryId).orElseThrow(()->new ResourceNotFoundException("category", "id", categoryId));

        //category uncategorized is fetched
        Category uncategorized=categoryRepository.findByCategoryName("Uncategorized").orElseThrow(()-> new ResourceNotFoundException("Uncategorized"));

        //all posts related to the category is put into the list of posts
        List<Post> posts=postRepository.findByCategory(category);

        //before deleting set the categories as uncategorized in the related posts
        for(Post post: posts){
            post.setCategory(uncategorized);
        }

        //save all the posts
        postRepository.saveAll(posts);

        //category repository now can e deleted
        categoryRepository.delete(category);

    }


    //convt entity to dto -> response send
    private CategoryDto mapToDto (Category category){
        CategoryDto categoryDto= new CategoryDto();
        categoryDto.setId(category.getId());
        categoryDto.setCategory_name(category.getCategoryName());
        categoryDto.setTitle(category.getTitle());
        return categoryDto;
    }

    //convt dto to entity
    private Category mapToEntity (CategoryDto categoryDto){
        Category category= new Category();
        category.setCategoryName(categoryDto.getCategory_name());
        category.setTitle(categoryDto.getTitle());
        return category;
    }
}

