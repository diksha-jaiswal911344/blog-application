package com.ql.BlogApplication.controllers;

import com.ql.BlogApplication.DTO.ApiResponseNew;
import com.ql.BlogApplication.DTO.LikeDto;
import com.ql.BlogApplication.services.LikeService;
import jakarta.validation.Valid;
//import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/likes")
public class LikeController {
    private LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PostMapping
    public ResponseEntity<ApiResponseNew<Map<String, String>>> toggleLike(@RequestBody @Valid LikeDto likeDto) {
        String response = likeService.toggleLike(likeDto);
        Map<String,String> data= new HashMap<>();
        data.put("message",response);
        return ResponseEntity.ok(
                ApiResponseNew.success(200, data, "Like status updated successfully")
        );
    }
}
