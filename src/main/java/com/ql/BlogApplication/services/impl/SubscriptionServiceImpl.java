package com.ql.BlogApplication.services.impl;

import com.ql.BlogApplication.DTO.SubscriptionDto;
import com.ql.BlogApplication.entities.Subscription;
import com.ql.BlogApplication.entities.User;
import com.ql.BlogApplication.entities.UserRole;
import com.ql.BlogApplication.exceptions.ResourceNotFoundException;
import com.ql.BlogApplication.repository.SubscriptionRepository;
import com.ql.BlogApplication.repository.UserRepository;
import com.ql.BlogApplication.services.SubscriptionService;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.logging.Logger;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(LikeServiceImpl.class);

    private UserRepository userRepository;
    private SubscriptionRepository subscriptionRepository;

    public SubscriptionServiceImpl(SubscriptionRepository subscriptionRepository, UserRepository userRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
    }

    @Override
    public String handleSubscription(SubscriptionDto dto) {
        User user=userRepository.findById(dto.getUserId()).orElseThrow(()->new ResourceNotFoundException("user","id",dto.getUserId()));
        User author=userRepository.findById(dto.getAuthorId()).orElseThrow(()->new ResourceNotFoundException("author","id", dto.getAuthorId()));
        // THE  REQUESTED dto author must be author check
        if(!author.getUserRoles().stream().anyMatch(userRole -> "author".equals(userRole.getRole().getName()))){
            throw new IllegalArgumentException("the given user is not author");
        }

        logger.info("req is arrived:{}",dto.isSubscribed());
        Optional<Subscription> existing= subscriptionRepository.findByUserAndAuthor(user,author);

        if(dto.isSubscribed()){
            if(existing.isPresent()){
                return "already subscribed by you";
            }

            Subscription subscription=new Subscription();
            subscription.setUser(user);
            subscription.setAuthor(author);
            subscriptionRepository.save(subscription);
            return "the author subscribed successfully";
        }
        else {
            //unsubscribe
            if (existing.isEmpty()) {
                throw new ResourceNotFoundException("Subscription", "authorId and userId", dto.getAuthorId() + " & " + dto.getUserId());
            }
            subscriptionRepository.delete(existing.get());
            return "author unsubscribed successfully";
        }
    }
}
