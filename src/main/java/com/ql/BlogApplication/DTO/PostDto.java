package com.ql.BlogApplication.DTO;

//Dto can also be named as payloads just becauser there meanings are same

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data

public class PostDto {
    private String id;

    @NotEmpty
    @Size(min = 2, message = "Post title should have at least 2 characters")
    private String title;

    @NotEmpty
    @Size(min = 10, message = "Post title should have at least 10 characters")
    private String content;

    @JsonProperty("isPublished")
    private boolean isPublished;

    private String categoryId;

    private String imageUrl;

}
