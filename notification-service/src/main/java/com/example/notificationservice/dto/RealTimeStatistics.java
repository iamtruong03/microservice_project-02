package com.example.notificationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RealTimeStatistics {
    private Long id;
    private String statType;  // "ORDERS", "TRANSACTIONS", "USERS", "INVENTORY"
    private Long totalCount;
    private Long successCount;
    private Long failureCount;
    private Double successRate;
    private Double avgProcessingTime;  // in milliseconds
    private LocalDateTime lastUpdated;
    private LocalDateTime createdAt;
    private String status;  // "ACTIVE", "WARNING", "CRITICAL"
}
