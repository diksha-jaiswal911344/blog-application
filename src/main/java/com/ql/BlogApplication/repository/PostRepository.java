package com.ql.BlogApplication.repository;

import com.ql.BlogApplication.documents.Category;
import com.ql.BlogApplication.documents.Post;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

//it provides suppert for pegination and shorting
public interface PostRepository extends MongoRepository<Post,String> {
    //
    List<Post> findByCategoryId(String categoryId);
    List<Post> findByCategory(Category category);
}
