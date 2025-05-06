package com.ql.BlogApplication.DTO;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryDto {
    private long id;
    @NotEmpty
    @Size(min = 2, message = "CategoryName title should have at least 2 characters")
    private String category_name;

    @NotEmpty
    @Size(min = 2, message = "CategoryName title should have at least 2 characters")
    private String title;

    // Add author ID field
    private Long authorId;

    // Add author username/email for display purposes
    private String authorName;
}
