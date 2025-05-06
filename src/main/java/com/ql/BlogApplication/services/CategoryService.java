package com.ql.BlogApplication.services;

import com.ql.BlogApplication.DTO.ApiResponseNew;
import com.ql.BlogApplication.DTO.CategoryDto;

import java.util.List;
import java.util.Map;

public interface CategoryService {

    ApiResponseNew<CategoryDto> createCategory(CategoryDto categoryDto, Long authorId);

    ApiResponseNew<List<CategoryDto>> getALLCategory();

    ApiResponseNew<CategoryDto> getCategoryById(Long id);

    ApiResponseNew<CategoryDto> updateCategory(CategoryDto categoryDto, Long id, Long currentUserId);

    ApiResponseNew<String> deleteCategory(Long categoryId, Long currentUserId);

    public boolean isAuthorOfCategory(Long categoryId, Long userId);
}
