package com.example.userservice.config;

import com.example.userservice.model.User;
import com.example.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

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
                log.info("Creating default admin user...");
                
                User adminUser = User.builder()
                        .userName("admin")
                        .password(passwordEncoder.encode("admin123"))
                        .email("admin@system.local")
                        .firstName("System")
                        .lastName("Administrator")
                        .dateOfBirth(LocalDate.of(2000, 1, 1))
                        .gender("MALE")
                        .address("System")
                        .city("System")
                        .state("System")
                        .postalCode("000000")
                        .country("System")
                        .isActive(true)
                        .isLocked(false)
                        .build();
                
                userRepository.save(adminUser);
                log.info("Default admin user created successfully");
                log.info("Username: admin");
                log.info("Password: admin123");
            } else {
                log.info("Admin user already exists, skipping initialization");
            }
        };
    }
}
