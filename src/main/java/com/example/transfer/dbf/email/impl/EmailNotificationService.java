package com.example.transfer.dbf.email.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailNotificationService {

    @Value("${email.recipients}")
    private String recipients;

    @Value("${email.sender}")
    private String sender;

    @Value("${email.error.cooldown.hours}")
    private int cooldownHours;

    private final JavaMailSender mailSender;

    private final Map<String, LocalDateTime> lastSentErrors = new HashMap<>();

    public void sendErrorNotification(String errorMessage) {
        if (shouldSendNotification(errorMessage)) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(sender);
            message.setTo(Arrays.stream(recipients.split(","))
                    .map(String::trim)
                    .toArray(String[]::new));

            message.setSubject("Ошибка миграции данных");
            message.setText(errorMessage);

            mailSender.send(message);
            // Обновляем время последней отправки
            lastSentErrors.put(errorMessage, LocalDateTime.now());
        }
    }

    private boolean shouldSendNotification(String errorMessage) {
        LocalDateTime lastSent = lastSentErrors.get(errorMessage);
        if (lastSent == null) {
            return true;
        }

        LocalDateTime now = LocalDateTime.now();
        return lastSent.plusHours(cooldownHours).isBefore(now);
    }

    public void markErrorAsResolved() {
        lastSentErrors.clear();
    }
}
