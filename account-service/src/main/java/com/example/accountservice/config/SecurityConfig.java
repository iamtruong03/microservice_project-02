package com.example.accountservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Service nghiệp vụ không cần xác thực JWT
        // JWT được xác thực ở API Gateway
        http
                .authorizeHttpRequests(authz -> authz
                        .anyRequest().permitAll()  // Allow all requests
                )
                .csrf().disable()
                .httpBasic().disable();

        return http.build();
    }
}
