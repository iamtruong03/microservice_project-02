package com.example.notificationservice.service;

import com.example.notificationservice.dto.RealTimeStatistics;
import com.example.notificationservice.dto.StatisticsReport;
import com.example.notificationservice.handler.StatisticsWebSocketHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
@EnableScheduling
public class StatisticsService {

    private StatisticsWebSocketHandler webSocketHandler;

    private final Random random = new Random();
    
    // Simulated stats storage (in production, use database)
    private RealTimeStatistics ordersStats;
    private RealTimeStatistics transactionsStats;
    private RealTimeStatistics usersStats;
    private RealTimeStatistics inventoryStats;

    public StatisticsService() {
        initializeStatistics();
    }

    @Autowired
    public void setWebSocketHandler(StatisticsWebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }

    private void initializeStatistics() {
        ordersStats = RealTimeStatistics.builder()
                .statType("ORDERS")
                .totalCount(0L)
                .successCount(0L)
                .failureCount(0L)
                .successRate(100.0)
                .avgProcessingTime(150.0)
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .lastUpdated(LocalDateTime.now())
                .build();

        transactionsStats = RealTimeStatistics.builder()
                .statType("TRANSACTIONS")
                .totalCount(0L)
                .successCount(0L)
                .failureCount(0L)
                .successRate(100.0)
                .avgProcessingTime(200.0)
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .lastUpdated(LocalDateTime.now())
                .build();

        usersStats = RealTimeStatistics.builder()
                .statType("USERS")
                .totalCount(0L)
                .successCount(0L)
                .failureCount(0L)
                .successRate(100.0)
                .avgProcessingTime(100.0)
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .lastUpdated(LocalDateTime.now())
                .build();

        inventoryStats = RealTimeStatistics.builder()
                .statType("INVENTORY")
                .totalCount(0L)
                .successCount(0L)
                .failureCount(0L)
                .successRate(100.0)
                .avgProcessingTime(120.0)
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .lastUpdated(LocalDateTime.now())
                .build();
    }

    @Scheduled(fixedDelay = 2000)  // Update every 2 seconds
    public void updateStatistics() {
        try {
            updateOrderStats();
            updateTransactionStats();
            updateUserStats();
            updateInventoryStats();

            // Generate complete report
            StatisticsReport report = generateReport();
            webSocketHandler.broadcastStatistics(report);

            log.debug("Statistics updated and broadcasted");
        } catch (Exception e) {
        }
    }

    private void updateOrderStats() {
        long newOrders = random.nextInt(20) + 5;
        long successOrders = (long)(newOrders * (0.92 + random.nextDouble() * 0.08));
        long failureOrders = newOrders - successOrders;

        ordersStats.setTotalCount(ordersStats.getTotalCount() + newOrders);
        ordersStats.setSuccessCount(ordersStats.getSuccessCount() + successOrders);
        ordersStats.setFailureCount(ordersStats.getFailureCount() + failureOrders);
        ordersStats.setSuccessRate(
                (ordersStats.getSuccessCount() * 100.0) / ordersStats.getTotalCount()
        );
        ordersStats.setAvgProcessingTime(
                120.0 + (random.nextDouble() * 60.0)
        );
        ordersStats.setLastUpdated(LocalDateTime.now());
        updateStatus(ordersStats);
    }

    private void updateTransactionStats() {
        long newTransactions = random.nextInt(50) + 20;
        long successTransactions = (long)(newTransactions * (0.95 + random.nextDouble() * 0.05));
        long failureTransactions = newTransactions - successTransactions;

        transactionsStats.setTotalCount(transactionsStats.getTotalCount() + newTransactions);
        transactionsStats.setSuccessCount(transactionsStats.getSuccessCount() + successTransactions);
        transactionsStats.setFailureCount(transactionsStats.getFailureCount() + failureTransactions);
        transactionsStats.setSuccessRate(
                (transactionsStats.getSuccessCount() * 100.0) / transactionsStats.getTotalCount()
        );
        transactionsStats.setAvgProcessingTime(
                180.0 + (random.nextDouble() * 80.0)
        );
        transactionsStats.setLastUpdated(LocalDateTime.now());
        updateStatus(transactionsStats);
    }

    private void updateUserStats() {
        long newUsers = random.nextInt(10) + 2;
        usersStats.setTotalCount(usersStats.getTotalCount() + newUsers);
        usersStats.setSuccessCount(usersStats.getTotalCount());
        usersStats.setFailureCount(0L);
        usersStats.setSuccessRate(100.0);
        usersStats.setAvgProcessingTime(
                80.0 + (random.nextDouble() * 40.0)
        );
        usersStats.setLastUpdated(LocalDateTime.now());
        updateStatus(usersStats);
    }

    private void updateInventoryStats() {
        long updates = random.nextInt(15) + 5;
        long successUpdates = (long)(updates * (0.90 + random.nextDouble() * 0.10));
        long failureUpdates = updates - successUpdates;

        inventoryStats.setTotalCount(inventoryStats.getTotalCount() + updates);
        inventoryStats.setSuccessCount(inventoryStats.getSuccessCount() + successUpdates);
        inventoryStats.setFailureCount(inventoryStats.getFailureCount() + failureUpdates);
        inventoryStats.setSuccessRate(
                (inventoryStats.getSuccessCount() * 100.0) / inventoryStats.getTotalCount()
        );
        inventoryStats.setAvgProcessingTime(
                100.0 + (random.nextDouble() * 50.0)
        );
        inventoryStats.setLastUpdated(LocalDateTime.now());
        updateStatus(inventoryStats);
    }

    private void updateStatus(RealTimeStatistics stats) {
        if (stats.getSuccessRate() >= 95 && stats.getAvgProcessingTime() <= 250) {
            stats.setStatus("ACTIVE");
        } else if (stats.getSuccessRate() >= 90 && stats.getAvgProcessingTime() <= 300) {
            stats.setStatus("WARNING");
        } else {
            stats.setStatus("CRITICAL");
        }
    }

    private StatisticsReport generateReport() {
        return StatisticsReport.builder()
                .reportId("REPORT_" + System.currentTimeMillis())
                .title("Real-Time System Statistics")
                .timestamp(System.currentTimeMillis())
                .systemHealth(determineSystemHealth())
                .statistics(List.of(ordersStats, transactionsStats, usersStats, inventoryStats))
                .totalTransactions(transactionsStats.getTotalCount())
                .totalOrders(ordersStats.getTotalCount())
                .activeUsers(usersStats.getTotalCount())
                .systemUptime(99.5 + (random.nextDouble() * 0.5))
                .build();
    }

    private String determineSystemHealth() {
        double avgSuccessRate = (
                ordersStats.getSuccessRate() +
                transactionsStats.getSuccessRate() +
                usersStats.getSuccessRate() +
                inventoryStats.getSuccessRate()
        ) / 4;

        if (avgSuccessRate >= 95) {
            return "HEALTHY";
        } else if (avgSuccessRate >= 90) {
            return "WARNING";
        } else {
            return "CRITICAL";
        }
    }

    public StatisticsReport getCurrentReport() {
        return generateReport();
    }

    public RealTimeStatistics getStatsByType(String type) {
        return switch (type.toUpperCase()) {
            case "ORDERS" -> ordersStats;
            case "TRANSACTIONS" -> transactionsStats;
            case "USERS" -> usersStats;
            case "INVENTORY" -> inventoryStats;
            default -> null;
        };
    }

    public int getActiveConnections() {
        return webSocketHandler.getActiveConnections();
    }
}
