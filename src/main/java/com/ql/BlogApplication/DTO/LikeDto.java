package com.ql.BlogApplication.DTO;

import lombok.Data;
import lombok.Getter;

@Data
public class LikeDto {
    private Long userId;
    private Long postId;

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

