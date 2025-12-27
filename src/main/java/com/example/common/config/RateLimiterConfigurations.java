package com.example.common.config;

import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Rate Limiter Configuration
 * Cấu hình các rate limiter profile cho ứng dụng
 */
@Configuration
public class RateLimiterConfigurations {

    @Bean
    public RateLimiterRegistry rateLimiterRegistry() {
        return RateLimiterRegistry.ofDefaults();
    }

    // Profile cho Registration API: 5 requests/minute
    public static RateLimiterConfig registrationRateLimiterConfig() {
        return RateLimiterConfig.custom()
            .limitRefreshPeriod(Duration.ofMinutes(1))
            .limitForPeriod(5)
            .timeoutDuration(Duration.ofSeconds(5))
            .build();
    }

    // Profile cho Login API: 10 requests/minute
    public static RateLimiterConfig loginRateLimiterConfig() {
        return RateLimiterConfig.custom()
            .limitRefreshPeriod(Duration.ofMinutes(1))
            .limitForPeriod(10)
            .timeoutDuration(Duration.ofSeconds(5))
            .build();
    }

    // Profile cho Order API: 100 requests/minute
    public static RateLimiterConfig orderRateLimiterConfig() {
        return RateLimiterConfig.custom()
            .limitRefreshPeriod(Duration.ofMinutes(1))
            .limitForPeriod(100)
            .timeoutDuration(Duration.ofSeconds(5))
            .build();
    }

    // Profile cho Public API: 50 requests/minute
    public static RateLimiterConfig publicRateLimiterConfig() {
        return RateLimiterConfig.custom()
            .limitRefreshPeriod(Duration.ofMinutes(1))
            .limitForPeriod(50)
            .timeoutDuration(Duration.ofSeconds(5))
            .build();
    }
}
