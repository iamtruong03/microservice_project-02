package com.example.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Security Configuration for API Gateway
 * Disables CSRF for REST API and enables stateless session management
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
            .csrf().disable()  // Disable CSRF for REST API (using JWT instead)
            .authorizeExchange()
                .pathMatchers("/api/auth/**").permitAll()
                .pathMatchers("/api/public/**").permitAll()
                .pathMatchers("/health").permitAll()
                .pathMatchers("/actuator/**").permitAll()
                .anyExchange().authenticated()
                .and()
            .httpBasic().disable()  // Disable HTTP Basic for REST API
            .formLogin().disable(); // Disable form login for REST API

        return http.build();
    }
}
