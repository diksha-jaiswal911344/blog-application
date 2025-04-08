package com.ql.BlogApplication.repository;

import com.ql.BlogApplication.entities.Post;
import org.springframework.data.jpa.repository.JpaRepository;

//it provides suppert for pegination and shorting
public interface PostRepository extends JpaRepository<Post,Long> {
    //
}
