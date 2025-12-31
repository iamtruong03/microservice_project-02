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
        log.info("User login attempt: {}", request.getUsername());
        
        UserDetail userDetail = userServiceClient.authenticate(request.getUsername(), request.getPassword());
        
        User user = userRepository.findByUsername(request.getUsername())
                .orElseGet(() -> {
                    log.info("Creating new user cache in Auth-Service: {}", request.getUsername());
                    User newUser = User.builder()
                            .username(request.getUsername())
                            .email(userDetail.getEmail())
                            .fullName(userDetail.getFirstName() + " " + userDetail.getLastName())
                            .enabled(true)
                            .createdAt(LocalDateTime.now().toString())
                            .updatedAt(LocalDateTime.now().toString())
                            .build();
                    return userRepository.save(newUser);
                });

        String token = tokenProvider.generateToken(user.getUsername(), userDetail.getId());
        
        log.info("User {} logged in successfully", request.getUsername());
        return JwtResponse.builder()
                .token(token)
                .id(userDetail.getId())
                .username(userDetail.getUserName())
                .email(userDetail.getEmail())
                .fullName(userDetail.getFirstName() + " " + userDetail.getLastName())
                .build();
    }

    public JwtResponse register(RegisterRequest request) {
        log.info("User registration attempt: {}", request.getUsername());
        
        UserDetail userDetail = userServiceClient.authenticate(request.getUsername(), request.getPassword());
        
        User user = userRepository.findByUsername(request.getUsername())
                .orElseGet(() -> {
                    log.info("Creating new user cache in Auth-Service: {}", request.getUsername());
                    User newUser = User.builder()
                            .username(request.getUsername())
                            .email(userDetail.getEmail())
                            .fullName(userDetail.getFirstName() + " " + userDetail.getLastName())
                            .enabled(true)
                            .createdAt(LocalDateTime.now().toString())
                            .updatedAt(LocalDateTime.now().toString())
                            .build();
                    return userRepository.save(newUser);
                });

        String token = tokenProvider.generateToken(user.getUsername(), userDetail.getId());
        log.info("User {} registered successfully", request.getUsername());
        return JwtResponse.builder()
                .token(token)
                .id(userDetail.getId())
                .username(userDetail.getUserName())
                .email(userDetail.getEmail())
                .fullName(userDetail.getFirstName() + " " + userDetail.getLastName())
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
