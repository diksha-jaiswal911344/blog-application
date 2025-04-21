package com.ql.BlogApplication.controllers;

import com.ql.BlogApplication.DTO.CategoryDto;
import com.ql.BlogApplication.entities.Category;
import com.ql.BlogApplication.services.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/categories")
public class CategoryController {
    private CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    //Creating category
    @PostMapping
    public ResponseEntity<CategoryDto> createCategory (@Valid @RequestBody CategoryDto categoryDto){
        return new ResponseEntity<>(categoryService.createCategory(categoryDto), HttpStatus.CREATED);
    }

    //get all categories api
    @GetMapping
    public List<CategoryDto> getAllCategories(){
        return categoryService.getALLCategory();
    }

    //get category by id
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getCategoriesById(@PathVariable(value = "id") long id){
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    // update category by id
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDto> updateCategory(@Valid @RequestBody CategoryDto categoryDto, @PathVariable(value = "id") long id){
        return new ResponseEntity<>(categoryService.updateCategory(categoryDto, id), HttpStatus.OK);
    }

}
