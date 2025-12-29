package com.example.notificationservice.controller;

import com.example.notificationservice.dto.RealTimeStatistics;
import com.example.notificationservice.dto.StatisticsReport;
import com.example.notificationservice.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/statistics")
@CrossOrigin(origins = "*")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/report")
    public ResponseEntity<StatisticsReport> getCurrentReport() {
        log.info("Getting current statistics report");
        return ResponseEntity.ok(statisticsService.getCurrentReport());
    }

    @GetMapping("/{type}")
    public ResponseEntity<RealTimeStatistics> getStatisticsByType(@PathVariable String type) {
        log.info("Getting statistics for type: {}", type);
        RealTimeStatistics stats = statisticsService.getStatsByType(type);
        if (stats != null) {
            return ResponseEntity.ok(stats);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/connections")
    public ResponseEntity<Integer> getActiveConnections() {
        return ResponseEntity.ok(statisticsService.getActiveConnections());
    }

    @GetMapping("/health")
    public ResponseEntity<String> getHealthStatus() {
        return ResponseEntity.ok(statisticsService.getCurrentReport().getSystemHealth());
    }
}
