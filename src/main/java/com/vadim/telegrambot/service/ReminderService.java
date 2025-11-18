package com.vadim.telegrambot.service;

import com.vadim.telegrambot.model.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReminderService {

    private final Bot bot;

    @Async("reminderExecutor")
    public void sendAsync(Payment payment, String template) {
        String text = String.format(template,
                payment.getSubscription().getName(),
                payment.getSubscription().getPrice());
        bot.sendText(payment.getUser().getTelegramId(), text);
    }
}
