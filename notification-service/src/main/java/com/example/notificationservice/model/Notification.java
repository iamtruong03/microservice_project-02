package com.example.notificationservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    private String id;
    private String recipientEmail;
    private String subject;
    private String message;
    private String status; // PENDING, SENT, FAILED
    private LocalDateTime createdAt;
    private LocalDateTime sentAt;
    private String createdBy;
    private String updatedBy;
}
