package com.ql.BlogApplication.controllers;

import com.ql.BlogApplication.DTO.ApiResponseNew;
import com.ql.BlogApplication.DTO.SubscriptionDto;
import com.ql.BlogApplication.services.SubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/subscriptions")
public class SubscriptionController {
    private SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PostMapping
    public ResponseEntity<ApiResponseNew<String>> handleSubscription(@RequestBody SubscriptionDto subscriptionDto) {
        String message = subscriptionService.handleSubscription(subscriptionDto);
        return ResponseEntity.ok(ApiResponseNew.success(200, true, "Subscription handled successfully", null));
    }
}
