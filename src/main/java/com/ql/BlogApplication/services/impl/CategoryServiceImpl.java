package com.ql.BlogApplication.services.impl;

import com.ql.BlogApplication.DTO.CategoryDto;
import com.ql.BlogApplication.documents.Category;
import com.ql.BlogApplication.documents.Post;
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
        Category category = mapToEntity(categoryDto);
        Category newCategory = categoryRepository.save(category);

        // we have to give response to the client
        return mapToDto(newCategory);
    }

    @Override
    public List<CategoryDto> getALLCategory() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream().map(category -> mapToDto(category)).collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryById(String id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("category", "id", id));
        return mapToDto(category);
    }

    @Override
    public CategoryDto updateCategory(CategoryDto categoryDto, String id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("category", "id", id));
        category.setTitle(categoryDto.getTitle());
        category.setCategoryName(categoryDto.getCategory_name());
        Category updatedCategory = categoryRepository.save(category);
        return mapToDto(updatedCategory);
    }

    @Override
    public void deleteCategory(String categoryId) {
        // Don't allow deleting "Uncategorized" category
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("category", "id", categoryId));

        if ("Uncategorized".equals(category.getCategoryName())) {
            throw new RuntimeException("Cannot delete the 'Uncategorized' category");
        }

        // Get uncategorized category
        Category uncategorized = categoryRepository.findByCategoryName("Uncategorized")
                .orElseThrow(() -> new ResourceNotFoundException("Category", "name", "Uncategorized"));

        // Get all posts related to the category
        List<Post> posts = postRepository.findByCategory(category);

        // Set the category as uncategorized in all related posts
        for (Post post : posts) {
            post.setCategory(uncategorized);
        }

        // Save all the posts
        postRepository.saveAll(posts);

        // Delete the category
        categoryRepository.delete(category);
    }

    //convert entity to dto -> response send
    private CategoryDto mapToDto(Category category){
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(category.getId());
        categoryDto.setCategory_name(category.getCategoryName());
        categoryDto.setTitle(category.getTitle());
        return categoryDto;
    }

    //convert dto to entity
    private Category mapToEntity(CategoryDto categoryDto){
        Category category = new Category();
        // Don't set the ID when creating a new entity, MongoDB will generate it
        // If ID exists in the DTO, only set it during updates
        if (categoryDto.getId() != null) {
            category.setId(categoryDto.getId());
        }
        category.setCategoryName(categoryDto.getCategory_name());
        category.setTitle(categoryDto.getTitle());
        return category;
    }
}