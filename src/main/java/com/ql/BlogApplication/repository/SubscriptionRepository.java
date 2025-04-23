package com.ql.BlogApplication.repository;

import com.ql.BlogApplication.entities.Subscription;
import com.ql.BlogApplication.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findByUserAndAuthor(User user, User author);

}
