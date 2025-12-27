package com.example.common.config;

import brave.Tracing;
import brave.baggage.BaggagePropagation;
import brave.propagation.B3Propagation;
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

    /**
     * Propagation - Cách truyền trace context giữa các service
     * B3Propagation: Standard format cho distributed tracing
     * BaggagePropagation: Custom baggage propagation
     */
    @Bean
    public brave.propagation.Propagation propagation() {
        return BaggagePropagation.create(
            B3Propagation.FACTORY,
            BaggagePropagation.newFactoryBuilder(B3Propagation.FACTORY)
                .add(brave.baggage.BaggagePropagationSetup.B3_SINGLE_NO_PARENT)
                .build()
        );
    }
}
