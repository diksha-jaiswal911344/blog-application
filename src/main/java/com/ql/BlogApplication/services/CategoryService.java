package com.ql.BlogApplication.services;

import com.ql.BlogApplication.DTO.CategoryDto;

import java.util.List;

public interface CategoryService {

CategoryDto createCategory(CategoryDto categoryDto);

List<CategoryDto> getALLCategory();

CategoryDto getCategoryById(Long id);

CategoryDto updateCategory(CategoryDto categoryDto, Long id);

}
