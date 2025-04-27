package com.ql.BlogApplication.repository;

import com.ql.BlogApplication.documents.Category;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CategoryRepository extends MongoRepository<Category, String> {
    Optional<Category> findByCategoryName(String categoryName);
}
