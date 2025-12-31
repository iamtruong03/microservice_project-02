package com.example.userservice.config;

import com.example.userservice.model.User;
import com.example.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataInitializationConfig {

    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initializeData(UserRepository userRepository) {
        return args -> {
            // Check if admin user already exists
            if (userRepository.findByUserName("admin").isEmpty()) {
                
                User adminUser = User.builder()
                        .userName("admin")
                        .password(passwordEncoder.encode("admin123"))
                        .email("admin@system.local")
                        .isActive(true)
                        .isLocked(false)
                        .build();
                
                userRepository.save(adminUser);
            } else {
            }
        };
    }
}
