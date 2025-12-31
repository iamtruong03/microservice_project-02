package com.example.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security Configuration
 * Cấu hình RBAC (Role-Based Access Control) và method-level security
 * 
 * Roles:
 * - ADMIN: Full access
 * - USER: Limited access
 * - GUEST: Read-only access
 */
@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(
    securedEnabled = true,
    jsr250Enabled = true,
    prePostEnabled = true
)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.disable())
            .csrf().disable()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
            .authorizeHttpRequests()
                .anyRequest().permitAll()
                .and()
            .httpBasic();

        return http.build();
    }

    /**
     * Predefined roles
     */
    public static class Roles {
        public static final String ADMIN = "ADMIN";
        public static final String USER = "USER";
        public static final String GUEST = "GUEST";
        public static final String MANAGER = "MANAGER";
        public static final String OPERATOR = "OPERATOR";
    }

    /**
     * Predefined authorities/permissions
     */
    public static class Permissions {
        // User permissions
        public static final String USER_READ = "USER_READ";
        public static final String USER_CREATE = "USER_CREATE";
        public static final String USER_UPDATE = "USER_UPDATE";
        public static final String USER_DELETE = "USER_DELETE";

        // Order permissions
        public static final String ORDER_READ = "ORDER_READ";
        public static final String ORDER_CREATE = "ORDER_CREATE";
        public static final String ORDER_UPDATE = "ORDER_UPDATE";
        public static final String ORDER_CANCEL = "ORDER_CANCEL";

        // Admin permissions
        public static final String SYSTEM_CONFIG = "SYSTEM_CONFIG";
        public static final String ROLE_MANAGEMENT = "ROLE_MANAGEMENT";
        public static final String AUDIT_LOG = "AUDIT_LOG";
    }
}
