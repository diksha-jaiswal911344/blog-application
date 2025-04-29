package com.ql.BlogApplication.repository;

import com.ql.BlogApplication.documents.Category;
import com.ql.BlogApplication.documents.Post;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

//it provides suppert for pegination and shorting
public interface PostRepository extends MongoRepository<Post,String> {
    //

    Optional<Post> findByTitle(String title);

    List<Post> findByCategoryId(String categoryId);
    List<Post> findByCategory(Category category);
}
