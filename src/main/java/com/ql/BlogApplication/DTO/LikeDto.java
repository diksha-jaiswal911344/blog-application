package com.ql.BlogApplication.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LikeDto {

    @NotNull
    private String userId;

    @NotNull
    private String postId;

    // lombok make getIsLike() not isLike() getter setter so this will case false value insertition from json .
    // so in case of boolean we must create custom getters and setters and this is happening because its name is isLike
    private boolean isLike;  // true = like, false = unlike

    public boolean isLike() {
        return isLike;
    }

    public void setIsLike(boolean isLike) {
        this.isLike = isLike;
    }

}

