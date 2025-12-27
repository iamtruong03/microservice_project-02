package com.example.common.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Circuit Breaker Configuration
 * Bảo vệ ứng dụng khỏi cascade failure từ các service gọi
 * 
 * States:
 * - CLOSED: Normal operation, requests được truyền đi
 * - OPEN: Service lỗi, requests được reject ngay lập tức
 * - HALF_OPEN: Recovery phase, thử request vài cái để kiểm tra
 */
@Configuration
public class CircuitBreakerConfigurations {

    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        return CircuitBreakerRegistry.ofDefaults();
    }

    /**
     * Circuit Breaker cho Order Service
     * - Failure rate > 50% → OPEN
     * - Wait 30s → HALF_OPEN
     * - 3 successful calls → CLOSED
     */
    public static CircuitBreakerConfig orderServiceCircuitBreakerConfig() {
        return CircuitBreakerConfig.custom()
            .failureRateThreshold(50.0f)
            .slowCallRateThreshold(50.0f)
            .slowCallDurationThreshold(Duration.ofSeconds(2))
            .waitDurationInOpenState(Duration.ofSeconds(30))
            .permittedNumberOfCallsInHalfOpenState(3)
            .minimumNumberOfCalls(5)
            .automaticTransitionFromOpenToHalfOpenEnabled(true)
            .build();
    }

    /**
     * Circuit Breaker cho Inventory Service
     */
    public static CircuitBreakerConfig inventoryServiceCircuitBreakerConfig() {
        return CircuitBreakerConfig.custom()
            .failureRateThreshold(40.0f)
            .slowCallRateThreshold(40.0f)
            .slowCallDurationThreshold(Duration.ofSeconds(3))
            .waitDurationInOpenState(Duration.ofSeconds(20))
            .permittedNumberOfCallsInHalfOpenState(2)
            .minimumNumberOfCalls(5)
            .automaticTransitionFromOpenToHalfOpenEnabled(true)
            .build();
    }

    /**
     * Circuit Breaker cho User Service
     */
    public static CircuitBreakerConfig userServiceCircuitBreakerConfig() {
        return CircuitBreakerConfig.custom()
            .failureRateThreshold(60.0f)
            .slowCallRateThreshold(60.0f)
            .slowCallDurationThreshold(Duration.ofSeconds(2))
            .waitDurationInOpenState(Duration.ofSeconds(15))
            .permittedNumberOfCallsInHalfOpenState(5)
            .minimumNumberOfCalls(3)
            .automaticTransitionFromOpenToHalfOpenEnabled(true)
            .build();
    }
}
