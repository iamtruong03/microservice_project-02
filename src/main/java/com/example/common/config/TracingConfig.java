package com.example.common.config;

import brave.sampler.Sampler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Distributed Tracing Configuration
 * Sử dụng Spring Cloud Sleuth & Brave
 * 
 * Features:
 * - Trace ID: Theo dõi request qua các service
 * - Span ID: Theo dõi các hoạt động cụ thể
 * - Baggage: Truyền context giữa các service
 */
@Slf4j
@Configuration
public class TracingConfig {

    /**
     * Sampler - Quyết định trace nào sẽ được gửi
     * ALWAYS_SAMPLE = 100% sampling (dev/test)
     * NEVER_SAMPLE = 0% sampling (không trace)
     * Custom sampler: % chance of sampling
     */
    @Bean
    public Sampler sampler() {
        return Sampler.ALWAYS_SAMPLE;
    }
}
