package com.ql.BlogApplication.controllers;

import com.ql.BlogApplication.DTO.ApiResponseNew;
import com.ql.BlogApplication.DTO.CategoryDto;
import com.ql.BlogApplication.services.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    //Creating category
    @PostMapping
    public ResponseEntity<ApiResponseNew<CategoryDto>> createCategory(@Valid @RequestBody CategoryDto categoryDto) {
        CategoryDto createdCategory = categoryService.createCategory(categoryDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseNew.success(201, createdCategory, "Category created successfully"));
    }

    //get all categories api
    @GetMapping
    public ResponseEntity<ApiResponseNew<Map<String, List<CategoryDto>>>> getAllCategories() {
        List<CategoryDto> categories = categoryService.getALLCategory();

        Map<String, List<CategoryDto>> responseData = new HashMap<>();
        responseData.put("availableCategories", categories);

        return ResponseEntity.ok(
                ApiResponseNew.success(200, responseData, "Categories fetched successfully")
        );
    }

    //get category by id
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseNew<CategoryDto>> getCategoriesById(@PathVariable("id") String id) {
        CategoryDto category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(
                ApiResponseNew.success(200, category, "Category fetched successfully")
        );
    }

    // update category by id
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseNew<Map<String, CategoryDto>>> updateCategory(
            @Valid @RequestBody CategoryDto categoryDto,
            @PathVariable("id") String id) {

        CategoryDto updatedCategory = categoryService.updateCategory(categoryDto, id);
        Map<String,CategoryDto> responseUser=new HashMap<>();
        responseUser.put("userUpdated",updatedCategory);
        return ResponseEntity.ok(
                ApiResponseNew.success(200, responseUser, "Category updated successfully")
        );
    }

    //delete category by id
    @DeleteMapping("/{category_id}")
    public ResponseEntity<ApiResponseNew<Map<String, String>>> deleteCategory(@PathVariable String category_id) {
        categoryService.deleteCategory(category_id);
        String message = "Category deleted successfully and related posts are assigned as 'Uncategorized'";
        Map<String,String > data= new HashMap<>();
        data.put("message",message);
        return ResponseEntity.ok(ApiResponseNew.success(200, data, "Deletion successful"));
    }
}