package com.example.common.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Async Service
 * Chứa các async operations cho email, notifications, v.v
 */
@Slf4j
@Service
public class AsyncService {

    /**
     * Gửi email một cách async (không chặn request)
     */
    @Async("emailExecutor")
    public CompletableFuture<Boolean> sendEmailAsync(String to, String subject, String body) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                log.info("📧 Sending email to: {} with subject: {}", to, subject);
                // Simulate email sending
                Thread.sleep(2000);
                log.info("✓ Email sent successfully to: {}", to);
                return true;
            } catch (InterruptedException e) {
                log.error("✗ Failed to send email", e);
                Thread.currentThread().interrupt();
                return false;
            }
        });
    }

    /**
     * Gửi notification một cách async
     */
    @Async("notificationExecutor")
    public CompletableFuture<Boolean> sendNotificationAsync(String userId, String message) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                log.info("🔔 Sending notification to user: {} - message: {}", userId, message);
                // Simulate notification sending
                Thread.sleep(1000);
                log.info("✓ Notification sent to user: {}", userId);
                return true;
            } catch (InterruptedException e) {
                log.error("✗ Failed to send notification", e);
                Thread.currentThread().interrupt();
                return false;
            }
        });
    }

    /**
     * Process data một cách async
     */
    @Async("taskExecutor")
    public CompletableFuture<String> processDataAsync(String data) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                log.info("⚙️ Processing data: {}", data);
                // Simulate heavy processing
                Thread.sleep(3000);
                String result = "Processed: " + data.toUpperCase();
                log.info("✓ Data processing completed: {}", result);
                return result;
            } catch (InterruptedException e) {
                log.error("✗ Failed to process data", e);
                Thread.currentThread().interrupt();
                return null;
            }
        });
    }

    /**
     * Batch processing một cách async
     */
    @Async("taskExecutor")
    public CompletableFuture<Integer> batchProcessAsync(int batchSize) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                log.info("📦 Starting batch processing with size: {}", batchSize);
                for (int i = 0; i < batchSize; i++) {
                    // Process each item
                    if (i % 10 == 0) {
                        log.info("  Progress: {}/{} items processed", i, batchSize);
                    }
                    Thread.sleep(100);
                }
                log.info("✓ Batch processing completed: {} items", batchSize);
                return batchSize;
            } catch (InterruptedException e) {
                log.error("✗ Batch processing interrupted", e);
                Thread.currentThread().interrupt();
                return 0;
            }
        });
    }
}
