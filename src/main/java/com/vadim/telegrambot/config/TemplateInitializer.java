package com.vadim.telegrambot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.vadim.telegrambot.model.Subscription;
import com.vadim.telegrambot.repository.SubscriptionRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TemplateInitializer {

    private final SubscriptionRepository repo;

    @Value("${telegram.subscription.vpnSubName}")
    private String VPN_SUB_NAME;
    @Value("${telegram.subscription.vpnPriceValue}")
    private int VPN_PRICE_VALUE;
    @Value("${telegram.subscription.vpnPaymentDay}")
    private int VPN_PAYMENT_DAY;

    @Value("${telegram.subscription.appleSubName}")
    private String APPLE_SUB_NAME;
    @Value("${telegram.subscription.applePriceValue}")
    private int APPLE_PRICE_VALUE;
    @Value("${telegram.subscription.applePaymentDay}")
    private int APPLE_PAYMENT_DAY;

    @PostConstruct
    public void init() {
        repo.deleteAll();
        repo.save(Subscription.builder()
                .name(VPN_SUB_NAME)
                .price(VPN_PRICE_VALUE)
                .dayOfMonth(VPN_PAYMENT_DAY)
                .build());
        repo.save(Subscription.builder()
                .name(APPLE_SUB_NAME)
                .price(APPLE_PRICE_VALUE)
                .dayOfMonth(APPLE_PAYMENT_DAY)
                .build());
    }
}
