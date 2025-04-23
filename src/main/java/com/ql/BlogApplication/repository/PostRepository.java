package com.ql.BlogApplication.repository;

import com.ql.BlogApplication.entities.Category;
import com.ql.BlogApplication.entities.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

//it provides suppert for pegination and shorting
public interface PostRepository extends JpaRepository<Post,Long> {
    //
    List<Post> findByCategoryId(Long categoryId);
    List<Post> findByCategory(Category category);
}
