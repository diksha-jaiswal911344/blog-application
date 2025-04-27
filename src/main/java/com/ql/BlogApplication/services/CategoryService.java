package com.ql.BlogApplication.services;

import com.ql.BlogApplication.DTO.CategoryDto;

import java.util.List;

public interface CategoryService {

    CategoryDto createCategory(CategoryDto categoryDto);

    List<CategoryDto> getALLCategory();

    CategoryDto getCategoryById(String id); // Changed from Long to String for MongoDB ObjectId

    CategoryDto updateCategory(CategoryDto categoryDto, String id); // Changed from Long to String

    void deleteCategory(String categoryId); // Changed from Long to String
}