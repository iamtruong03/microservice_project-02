package com.example.authservice.service;

import com.example.authservice.client.UserServiceClient;
import com.example.authservice.domain.User;
import com.example.authservice.dto.JwtResponse;
import com.example.authservice.dto.LoginRequest;
import com.example.authservice.dto.RegisterRequest;
import com.example.authservice.dto.UserDetail;
import com.example.authservice.repository.UserRepository;
import com.example.authservice.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtTokenProvider tokenProvider;
    private final UserServiceClient userServiceClient;

    public JwtResponse login(LoginRequest request) {
        log.info("User login attempt: {}", request.getUserName());
        
        UserDetail userDetail = userServiceClient.authenticate(request.getUserName(), request.getPassword());
        
        User user = userRepository.findByUsername(request.getUserName())
                .orElseGet(() -> {
                    log.info("Creating new user cache in Auth-Service: {}", request.getUserName());
                    String cachedFullName = (userDetail.getFirstName() != null ? userDetail.getFirstName() : "") + " " + 
                                           (userDetail.getLastName() != null ? userDetail.getLastName() : "");
                    User newUser = User.builder()
                            .username(request.getUserName())
                            .email(userDetail.getEmail())
                            .fullName(cachedFullName.trim())
                            .enabled(true)
                            .createdAt(LocalDateTime.now().toString())
                            .updatedAt(LocalDateTime.now().toString())
                            .build();
                    return userRepository.save(newUser);
                });

        String token = tokenProvider.generateToken(user.getUsername(), userDetail.getId());
        
        String fullName = (userDetail.getFirstName() != null ? userDetail.getFirstName() : "") + " " + 
                         (userDetail.getLastName() != null ? userDetail.getLastName() : "");
        
        log.info("User {} logged in successfully", request.getUserName());
        return JwtResponse.builder()
                .token(token)
                .type("Bearer")
                .id(userDetail.getId())
                .username(userDetail.getUserName())
                .email(userDetail.getEmail())
                .fullName(fullName.trim())
                .build();
    }

    public JwtResponse register(RegisterRequest request) {
        log.info("User registration attempt: {}", request.getUserName());
        
        UserDetail userDetail = userServiceClient.authenticate(request.getUserName(), request.getPassword());
        
        User user = userRepository.findByUsername(request.getUserName())
                .orElseGet(() -> {
                    log.info("Creating new user cache in Auth-Service: {}", request.getUserName());
                    String cachedFullName = (userDetail.getFirstName() != null ? userDetail.getFirstName() : "") + " " + 
                                           (userDetail.getLastName() != null ? userDetail.getLastName() : "");
                    User newUser = User.builder()
                            .username(request.getUserName())
                            .email(userDetail.getEmail())
                            .fullName(cachedFullName.trim())
                            .enabled(true)
                            .createdAt(LocalDateTime.now().toString())
                            .updatedAt(LocalDateTime.now().toString())
                            .build();
                    return userRepository.save(newUser);
                });

        String token = tokenProvider.generateToken(user.getUsername(), userDetail.getId());
        
        String fullName = (userDetail.getFirstName() != null ? userDetail.getFirstName() : "") + " " + 
                         (userDetail.getLastName() != null ? userDetail.getLastName() : "");
        
        log.info("User {} registered successfully", request.getUserName());
        return JwtResponse.builder()
                .token(token)
                .type("Bearer")
                .id(userDetail.getId())
                .username(userDetail.getUserName())
                .email(userDetail.getEmail())
                .fullName(fullName.trim())
                .build();
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
