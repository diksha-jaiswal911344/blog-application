package com.ql.BlogApplication.controllers;

import com.ql.BlogApplication.DTO.LikeDto;
import com.ql.BlogApplication.services.LikeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/likes")
public class LikeController {
    private LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PostMapping
    public ResponseEntity<String> toggleLike(@RequestBody LikeDto likeDto){
        String response=likeService.toggleLike(likeDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
