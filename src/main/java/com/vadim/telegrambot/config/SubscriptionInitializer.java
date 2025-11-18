package com.vadim.telegrambot.config;

import com.vadim.telegrambot.dto.SubscriptionTemplate;
import com.vadim.telegrambot.model.Subscription;
import com.vadim.telegrambot.model.User;
import com.vadim.telegrambot.repository.SubscriptionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SubscriptionInitializer {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionJsonLoader loader;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void onApplicationReady() {
        List<SubscriptionTemplate> subscriptions = loader.loadSubscriptions();

        for (SubscriptionTemplate template : subscriptions) {
            initOrUpdate(template);
        }

        removeOutdated(subscriptions);
    }

    private void initOrUpdate(SubscriptionTemplate template) {
        Subscription sub = subscriptionRepository.findByUniqueCode(template.getUniqueCode()).orElseGet(Subscription::new);

        sub.setUniqueCode(template.getUniqueCode());
        sub.setName(template.getName());
        sub.setPrice(template.getPrice());
        sub.setDayOfMonth(template.getDayOfMonth());

        subscriptionRepository.save(sub);
    }

    private void removeOutdated(List<SubscriptionTemplate> subscriptions) {
        Set<String> validCodes = subscriptions.stream()
                .map(SubscriptionTemplate::getUniqueCode)
                .collect(Collectors.toSet());

        List<Subscription> all = subscriptionRepository.findAll();

        for (Subscription sub : all) {
            if (!validCodes.contains(sub.getUniqueCode())) {
                for (User user : sub.getUsers()) {
                    user.getSubscriptions().remove(sub);
                }
                sub.getUsers().clear();
                subscriptionRepository.delete(sub);
            }
        }
    }
}
