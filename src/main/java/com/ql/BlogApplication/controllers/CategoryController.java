package com.ql.BlogApplication.controllers;

import com.ql.BlogApplication.DTO.ApiResponseNew;
import com.ql.BlogApplication.DTO.CategoryDto;
import com.ql.BlogApplication.services.CategoryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("api/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // Helper method to get current user ID from request
    private Long getCurrentUserId(HttpServletRequest request) {
        Object userIdObj = request.getAttribute("userId");
        if (userIdObj == null) {
            return null;
        }
        return (Long) userIdObj;
    }

    // Creating category - only for authenticated users
    @PostMapping
    public ResponseEntity<ApiResponseNew<CategoryDto>> createCategory(@Valid @RequestBody CategoryDto categoryDto, HttpServletRequest request) {

        Long currentUserId = getCurrentUserId(request);
        if (currentUserId == null) {
            ApiResponseNew<CategoryDto> errorResponse = ApiResponseNew.success(401, false, "Authentication required", null);
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }
        ApiResponseNew<CategoryDto> response = categoryService.createCategory(categoryDto, currentUserId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Get all categories API - public endpoint
    @GetMapping
    public ResponseEntity<ApiResponseNew<List<CategoryDto>>> getAllCategories() {
        ApiResponseNew<List<CategoryDto>> responseData = categoryService.getALLCategory();
        return new ResponseEntity<>(responseData, HttpStatus.valueOf(responseData.getHttpStatusCode()));
    }

    // Get category by id - public endpoint
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseNew<CategoryDto>> getCategoriesById(@PathVariable("id") long id) {
        ApiResponseNew<CategoryDto> response = categoryService.getCategoryById(id);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getHttpStatusCode()));
    }

    // Update category by id - only for authenticated users who own the category
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseNew<CategoryDto>> updateCategory(@Valid @RequestBody CategoryDto categoryDto, @PathVariable("id") long id, HttpServletRequest request) {

        Long currentUserId = getCurrentUserId(request);
        if (currentUserId == null) {
            ApiResponseNew<CategoryDto> errorResponse = ApiResponseNew.success(401, false, "Authentication required", null);
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }
        ApiResponseNew<CategoryDto> response = categoryService.updateCategory(categoryDto, id, currentUserId);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getHttpStatusCode()));
    }

    // Delete category by id - only for authenticated users who own the category
    @DeleteMapping("/{category_id}")
    public ResponseEntity<ApiResponseNew<String>> deleteCategory(@PathVariable Long category_id, HttpServletRequest request) {

        Long currentUserId = getCurrentUserId(request);
        if (currentUserId == null) {
            ApiResponseNew<String> errorResponse = ApiResponseNew.success(401, false, "Authentication required", null);
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }
        ApiResponseNew<String> response = categoryService.deleteCategory(category_id, currentUserId);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getHttpStatusCode()));
    }
}