package com.ql.BlogApplication.repository;

import com.ql.BlogApplication.documents.Subscription;
import com.ql.BlogApplication.documents.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SubscriptionRepository extends MongoRepository<Subscription, String> {
    Optional<Subscription> findByUserAndAuthor(User user, User author);
    // You might also want to add:
    Optional<Subscription> findByUserIdAndAuthorId(String userId, String authorId);

}
