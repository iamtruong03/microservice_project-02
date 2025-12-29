package com.example.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRoutesConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Auth Service Routes (with stripPrefix)
                .route("auth-login", r -> r
                        .path("/api/auth/login")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://auth-service"))
                .route("auth-register", r -> r
                        .path("/api/auth/register")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://auth-service"))
                .route("auth-validate", r -> r
                        .path("/api/auth/validate")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://auth-service"))

                // Order Service Routes
                .route("order-service", r -> r
                        .path("/api/orders/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://order-service"))

                // Inventory Service Routes
                .route("inventory-service", r -> r
                        .path("/api/inventory/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://inventory-service"))

                // Accounting Service Routes
                .route("accounting-service", r -> r
                        .path("/api/accounting/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://accounting-service"))

                // Notification Service Routes
                .route("notification-service", r -> r
                        .path("/api/notifications/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://notification-service"))

                // User Service Routes
                .route("user-service", r -> r
                        .path("/api/users/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://user-service"))

                // Eureka Routes
                .route("eureka-service", r -> r
                        .path("/eureka/**")
                        .uri("http://localhost:8761"))

                .build();
    }
}
