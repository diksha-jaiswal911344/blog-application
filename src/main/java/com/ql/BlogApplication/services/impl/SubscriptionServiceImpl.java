package com.ql.BlogApplication.services.impl;

import com.ql.BlogApplication.DTO.SubscriptionDto;
import com.ql.BlogApplication.documents.Subscription;
import com.ql.BlogApplication.documents.User;
import com.ql.BlogApplication.exceptions.ResourceNotFoundException;
import com.ql.BlogApplication.repository.SubscriptionRepository;
import com.ql.BlogApplication.repository.UserRepository;
import com.ql.BlogApplication.services.SubscriptionService;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(SubscriptionServiceImpl.class);

    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionServiceImpl(SubscriptionRepository subscriptionRepository, UserRepository userRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
    }

    @Override
    public String handleSubscription(SubscriptionDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("user", "id", dto.getUserId()));
        User author = userRepository.findById(dto.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException("author", "id", dto.getAuthorId()));

        // Check if the requested user is an author
        // In MongoDB, the role is directly referenced in the User document
        if (author.getRole() == null || !"author".equals(author.getRole().getName())) {
            throw new IllegalArgumentException("The given user is not an author");
        }

        logger.info("Subscription request received: {}", dto.isSubscribed());

        // Using the repository method for MongoDB
        Optional<Subscription> existing = subscriptionRepository.findByUserAndAuthor(user, author);

        if (dto.isSubscribed()) {
            if (existing.isPresent()) {
                return "Already subscribed to this author";
            }

            Subscription subscription = new Subscription();
            subscription.setUser(user);
            subscription.setAuthor(author);
            subscriptionRepository.save(subscription);
            return "Author subscribed successfully";
        } else {
            // Unsubscribe
            if (existing.isEmpty()) {
                throw new ResourceNotFoundException("Subscription", "authorId and userId",
                        dto.getAuthorId() + " & " + dto.getUserId());
            }
            subscriptionRepository.delete(existing.get());
            return "Author unsubscribed successfully";
        }
    }
}