package com.ql.BlogApplication.services;

import com.ql.BlogApplication.DTO.LikeDto;

public interface LikeService {
    String toggleLike(LikeDto likeDto);
}
