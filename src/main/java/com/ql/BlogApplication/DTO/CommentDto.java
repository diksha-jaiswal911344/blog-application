package com.ql.BlogApplication.DTO;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentDto {
    private long id;

    @NotEmpty
    @Size(min = 2, message = "CommentName title should have at least 2 characters")
    private String comment_content;

    private long post_id;
    private long user_id;
}
