package com.ql.BlogApplication.controllers;

import com.ql.BlogApplication.DTO.ApiResponse;
import com.ql.BlogApplication.DTO.SubscriptionDto;
import com.ql.BlogApplication.entities.Subscription;
import com.ql.BlogApplication.services.SubscriptionService;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<String> handleSubscription(@RequestBody SubscriptionDto subscriptionDto){
        String message=subscriptionService.handleSubscription(subscriptionDto);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }
}
