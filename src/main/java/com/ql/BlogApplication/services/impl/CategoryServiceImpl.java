package com.ql.BlogApplication.services.impl;

import com.ql.BlogApplication.DTO.CategoryDto;
import com.ql.BlogApplication.entities.Category;
import com.ql.BlogApplication.exceptions.ResourceNotFoundException;
import com.ql.BlogApplication.repository.CategoryRepository;
import com.ql.BlogApplication.services.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
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
        category.setCategory_name(categoryDto.getCategory_name());
        Category updatedCategory=categoryRepository.save(category);
        return mapToDto(updatedCategory);
    }

    //convt entity to dto -> response send
    private CategoryDto mapToDto (Category category){
        CategoryDto categoryDto= new CategoryDto();
        categoryDto.setId(category.getId());
        categoryDto.setCategory_name(category.getCategory_name());
        categoryDto.setTitle(category.getTitle());
        return categoryDto;
    }

    //convt dto to entity
    private Category mapToEntity (CategoryDto categoryDto){
        Category category= new Category();
        category.setCategory_name(categoryDto.getCategory_name());
        category.setTitle(categoryDto.getTitle());
        return category;
    }
}
