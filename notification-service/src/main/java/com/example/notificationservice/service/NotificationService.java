package com.example.notificationservice.service;

import com.example.notificationservice.model.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    public Notification sendNotification(String recipientEmail, String subject, String message) {
        Notification notification = new Notification();
        notification.setId(UUID.randomUUID().toString());
        notification.setRecipientEmail(recipientEmail);
        notification.setSubject(subject);
        notification.setMessage(message);
        notification.setStatus("PENDING");
        notification.setCreatedAt(LocalDateTime.now());

        // Simulate sending email
        simulateSendEmail(notification);

        return notification;
    }

    // @KafkaListener(topics = "account-events", groupId = "notification-service-group") // Tắt Kafka
    public void handleAccountEvents(String message) {
        log.info("Received account event: {}", message);
        // Send notification about account event (account created, updated)
        sendNotification("customer@example.com", "Account Update", "Your account has been updated: " + message);
    }

    // @KafkaListener(topics = "user-transfer-events", groupId = "notification-service-group") // Tắt Kafka
    public void handleTransferEvents(String message) {
        log.info("Received transfer event: {}", message);
        // Send notification about transfer event
        sendNotification("customer@example.com", "Transfer Update", "Your transfer has been processed: " + message);
    }

    // @KafkaListener(topics = "user-events", groupId = "notification-service-group") // Tắt Kafka
    public void handleUserEvents(String message) {
        log.info("Received user event: {}", message);
        // Send notification about user event (user created, profile updated, role changed)
        sendNotification("customer@example.com", "User Event", "Your user account has been updated: " + message);
    }

    private void simulateSendEmail(Notification notification) {
        try {
            // Simulate email sending delay
            Thread.sleep(100);
            notification.setStatus("SENT");
            notification.setSentAt(LocalDateTime.now());
            log.info("Email sent to: {} with subject: {}", notification.getRecipientEmail(), notification.getSubject());
        } catch (InterruptedException e) {
            notification.setStatus("FAILED");
            log.error("Failed to send email to: {}", notification.getRecipientEmail(), e);
        }
    }
}
