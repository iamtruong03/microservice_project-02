package com.example.authservice.config;

import com.example.authservice.domain.User;
import com.example.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
public class DataInitializationConfig {
    private final UserRepository userRepository;

    @Bean
    public CommandLineRunner initializeData() {
        return args -> {
            if (!userRepository.existsByUsername("admin")) {
                User adminUser = User.builder()
                        .username("admin")
                        .email("admin@example.com")
                        .fullName("Administrator")
                        .enabled(true)
                        .createdAt(LocalDateTime.now().toString())
                        .updatedAt(LocalDateTime.now().toString())
                        .build();
                
                userRepository.save(adminUser);
                System.out.println("Admin user cached in Auth-Service");
            }
        };
    }
}
