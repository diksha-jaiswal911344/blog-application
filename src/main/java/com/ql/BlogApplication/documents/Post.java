package com.ql.BlogApplication.documents;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "posts")
public class Post {
    @Id
    private String id;

    private String title;

    private String content;

    private boolean isPublished;

    private String imageUrl;

    @DBRef
    private Category category;

    // Alternative is to just store the category ID
    // private String categoryId;

    // Comments and likes will reference this post instead of being stored here
}