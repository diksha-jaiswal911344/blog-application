//package com.ql.BlogApplication.services.impl;
//
//import com.ql.BlogApplication.DTO.CategoryDto;
//import com.ql.BlogApplication.entities.Category;
//import com.ql.BlogApplication.entities.Post;
//import com.ql.BlogApplication.exceptions.ResourceNotFoundException;
//import com.ql.BlogApplication.repository.CategoryRepository;
//import com.ql.BlogApplication.repository.PostRepository;
//import com.ql.BlogApplication.services.CategoryService;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//public class CategoryServiceImpl implements CategoryService {
//
//    private CategoryRepository categoryRepository;
//
//    private PostRepository postRepository;
//
//    public CategoryServiceImpl(CategoryRepository categoryRepository, PostRepository postRepository) {
//        this.categoryRepository = categoryRepository;
//        this.postRepository = postRepository;
//    }
//
//    @Override
//    public CategoryDto createCategory(CategoryDto categoryDto) {
//        //we have to save to db
//        Category category=mapToEntity(categoryDto);
//        Category newCategory= categoryRepository.save(category);
//
//        // we have to give respose to the client
//        return mapToDto(newCategory);
//    }
//
//    @Override
//    public List<CategoryDto> getALLCategory() {
//        List<Category> categories= categoryRepository.findAll();
//        return categories.stream().map(category -> mapToDto(category)).collect(Collectors.toList());
//    }
//
//    @Override
//    public CategoryDto getCategoryById(Long id) {
//        Category category=categoryRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("category","id",id));
//        return mapToDto(category);
//    }
//
//    @Override
//    public CategoryDto updateCategory(CategoryDto categoryDto, Long id) {
//        Category category=categoryRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("category","id",id));
//        category.setTitle(categoryDto.getTitle());
//        category.setCategoryName(categoryDto.getCategory_name());
//        Category updatedCategory=categoryRepository.save(category);
//        return mapToDto(updatedCategory);
//    }
//
//    @Override
//    public void deleteCategory(Long categoryId) {
//        //checking whether category is there in db or not
//        Category category=categoryRepository.findById(categoryId).orElseThrow(()->new ResourceNotFoundException("category", "id", categoryId));
//
//        //category uncategorized is fetched
//        Category uncategorized=categoryRepository.findByCategoryName("Uncategorized").orElseThrow(()-> new ResourceNotFoundException("Uncategorized"));
//
//        //all posts related to the category is put into the list of posts
//        List<Post> posts=postRepository.findByCategory(category);
//
//        //before deleting set the categories as uncategorized in the related posts
//        for(Post post: posts){
//            post.setCategory(uncategorized);
//        }
//
//        //save all the posts
//        postRepository.saveAll(posts);
//
//        //category repository now can e deleted
//        categoryRepository.delete(category);
//
//    }
//
//
//    //convt entity to dto -> response send
//    private CategoryDto mapToDto (Category category){
//        CategoryDto categoryDto= new CategoryDto();
//        categoryDto.setId(category.getId());
//        categoryDto.setCategory_name(category.getCategoryName());
//        categoryDto.setTitle(category.getTitle());
//        return categoryDto;
//    }
//
//    //convt dto to entity
//    private Category mapToEntity (CategoryDto categoryDto){
//        Category category= new Category();
//        category.setCategoryName(categoryDto.getCategory_name());
//        category.setTitle(categoryDto.getTitle());
//        return category;
//    }
//}
//

package com.ql.BlogApplication.services.impl;

import com.ql.BlogApplication.DTO.ApiResponseNew;
import com.ql.BlogApplication.DTO.CategoryDto;
import com.ql.BlogApplication.entities.Category;
import com.ql.BlogApplication.entities.Post;
import com.ql.BlogApplication.entities.User;
import com.ql.BlogApplication.exceptions.ResourceNotFoundException;
import com.ql.BlogApplication.repository.CategoryRepository;
import com.ql.BlogApplication.repository.PostRepository;
import com.ql.BlogApplication.repository.UserRepository;
import com.ql.BlogApplication.services.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private CategoryRepository categoryRepository;
    private PostRepository postRepository;
    private UserRepository userRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository, PostRepository postRepository, UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ApiResponseNew<CategoryDto> createCategory(CategoryDto categoryDto, Long authorId) {
        Optional<User> authorOpt = userRepository.findById(authorId);

        if (authorOpt.isEmpty()) {
            return ApiResponseNew.success(404, false, "User not found", null);
        }

        User author = authorOpt.get();

        Category category = mapToEntity(categoryDto);
        category.setAuthor(author);

        Category savedCategory = categoryRepository.save(category);

        return ApiResponseNew.success(201, true, "Category created successfully", mapToDto(savedCategory));
    }

    //for getting all categories
    @Override
    public ApiResponseNew<List<CategoryDto>> getALLCategory() {
        List<Category> categories = categoryRepository.findAll();
        List<CategoryDto> dtos = categories.stream().map(this::mapToDto).collect(Collectors.toList());
        return ApiResponseNew.success(200, true, "Categories fetched successfully", dtos);
    }

    //for getting category by id
    @Override
    public ApiResponseNew<CategoryDto> getCategoryById(Long id) {
        Category categoryOpt = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        return ApiResponseNew.success(200, true, "Category fetched successfully", mapToDto(categoryOpt));
    }

    //for updating category
    @Override
    public ApiResponseNew<CategoryDto> updateCategory(CategoryDto categoryDto, Long id, Long currentUserId) {
        Optional<Category> categoryOpt = categoryRepository.findById(id);

        if (categoryOpt.isEmpty()) {
            return ApiResponseNew.success(404, false, "Category not found", null);
        }

        Category category = categoryOpt.get();

        if (!category.getAuthor().getId().equals(currentUserId)) {
            return ApiResponseNew.success(403, false, "You are not authorized to update this category", null);
        }

        category.setTitle(categoryDto.getTitle());
        category.setCategoryName(categoryDto.getCategory_name());

        Category updatedCategory = categoryRepository.save(category);

        return ApiResponseNew.success(200, true, "Category updated successfully", mapToDto(updatedCategory));
    }

    //for deleting category
    @Override
    public ApiResponseNew<String> deleteCategory(Long categoryId, Long currentUserId) {
        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);

        if (categoryOpt.isEmpty()) {
            return ApiResponseNew.success(404, false, "Category not found", null);
        }

        Category category = categoryOpt.get();

        if (!category.getAuthor().getId().equals(currentUserId)) {
            return ApiResponseNew.success(403, false, "You are not authorized to delete this category", null);
        }

        Optional<Category> uncategorizedOpt = categoryRepository.findByCategoryName("Uncategorized");

        if (uncategorizedOpt.isEmpty()) {
            return ApiResponseNew.success(500, false, "Uncategorized category not found", null);
        }

        Category uncategorized = uncategorizedOpt.get();

        List<Post> posts = postRepository.findByCategory(category);

        for (Post post : posts) {
            post.setCategory(uncategorized);
        }

        postRepository.saveAll(posts);

        categoryRepository.delete(category);

        return ApiResponseNew.success(200, true, "Category deleted successfully and posts reassigned", "Success");
    }

    //checking is the author a valid author
    @Override
    public boolean isAuthorOfCategory(Long categoryId, Long userId) {
        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);

        return categoryOpt.map(category -> category.getAuthor().getId().equals(userId)).orElse(false);
    }

    // Convert entity to DTO
    private CategoryDto mapToDto(Category category) {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(category.getId());
        categoryDto.setCategory_name(category.getCategoryName());
        categoryDto.setTitle(category.getTitle());

        if (category.getAuthor() != null) {
            categoryDto.setAuthorId(category.getAuthor().getId());
            categoryDto.setAuthorName(category.getAuthor().getName());
        }

        return categoryDto;
    }

    // Convert DTO to entity
    private Category mapToEntity(CategoryDto categoryDto) {
        Category category = new Category();
        category.setCategoryName(categoryDto.getCategory_name());
        category.setTitle(categoryDto.getTitle());
        return category;
    }
}