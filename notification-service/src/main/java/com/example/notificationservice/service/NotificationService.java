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

    @KafkaListener(topics = "order-events", groupId = "notification-service-group")
    public void handleOrderEvents(String message) {
        log.info("Received order event: {}", message);
        // Send notification about order event
        sendNotification("customer@example.com", "Order Update", "Your order has been updated: " + message);
    }

    @KafkaListener(topics = "inventory-events", groupId = "notification-service-group")
    public void handleInventoryEvents(String message) {
        log.info("Received inventory event: {}", message);
        // Send notification about inventory event
        sendNotification("admin@example.com", "Inventory Update", "Inventory has been updated: " + message);
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
