package com.vadim.telegrambot.scheduler;

import com.vadim.telegrambot.model.Payment;
import com.vadim.telegrambot.service.PaymentService;
import com.vadim.telegrambot.service.ReminderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReminderScheduler {

    private final PaymentService paymentService;
    private final ReminderService reminderService;

    @Transactional
    @Scheduled(cron = "0 0 9 * * *")
    public void sendReminders() {
        LocalDate today = LocalDate.now();

        List<Payment> payments = paymentService.findAllDebtors();

        for (Payment payment : payments) {
            LocalDate due = LocalDate.of(
                    payment.getYear(),
                    payment.getMonth(),
                    payment.getSubscription().getDayOfMonth());

            if (today.equals(due.minusDays(1))) {
                reminderService.sendAsync(payment, "Привет " + payment.getUser().getFirstName()
                        + "! Напоминаю: завтра оплата «%s» — %s ₽");
            } else if (today.equals(due)) {
                reminderService.sendAsync(payment, "Привет " + payment.getUser().getFirstName()
                        + "! Напоминаю: сегодня оплата «%s» — %s ₽");
            } else if (today.isAfter(due)) {
                reminderService.sendAsync(payment, "Привет " + payment.getUser().getFirstName()
                        + "! Напоминаю: просрочен платеж «%s» — %s ₽. Оплати пожалуйста \uD83E\uDD72");
            }
        }
    }

    @Scheduled(cron = "0 5 0 1 * *")
    void scheduleMonthlyPayments() {
        paymentService.generatePaymentsForCurrentMonth();
    }
}
