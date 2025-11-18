package com.vadim.telegrambot.repository;

import com.vadim.telegrambot.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findByUniqueCode(String uniqueCode);
}
