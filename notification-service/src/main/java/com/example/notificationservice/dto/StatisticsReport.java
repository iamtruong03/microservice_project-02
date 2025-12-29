package com.example.notificationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsReport {
    private String reportId;
    private String title;
    private Long timestamp;
    private String systemHealth;  // "HEALTHY", "WARNING", "CRITICAL"
    private List<RealTimeStatistics> statistics;
    private Long totalTransactions;
    private Long totalOrders;
    private Long activeUsers;
    private Double systemUptime;
}
