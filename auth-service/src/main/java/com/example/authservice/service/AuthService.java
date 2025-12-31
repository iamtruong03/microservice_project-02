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
    private final TokenBlacklistService tokenBlacklistService;

    public JwtResponse login(LoginRequest request) {
        
        UserDetail userDetail = userServiceClient.authenticate(request.getUserName(), request.getPassword());
        
        User user = userRepository.findByUsername(request.getUserName())
                .orElseGet(() -> {
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
        String refreshToken = tokenProvider.generateRefreshToken(user.getUsername(), userDetail.getId());
        
        String fullName = (userDetail.getFirstName() != null ? userDetail.getFirstName() : "") + " " + 
                         (userDetail.getLastName() != null ? userDetail.getLastName() : "");
        
        return JwtResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .type("Bearer")
                .id(userDetail.getId())
                .userName(userDetail.getUserName())
                .email(userDetail.getEmail())
                .fullName(fullName.trim())
                .build();
    }

    public JwtResponse register(RegisterRequest request) {
        
        UserDetail userDetail = userServiceClient.authenticate(request.getUserName(), request.getPassword());
        
        User user = userRepository.findByUsername(request.getUserName())
                .orElseGet(() -> {
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
        String refreshToken = tokenProvider.generateRefreshToken(user.getUsername(), userDetail.getId());
        
        String fullName = (userDetail.getFirstName() != null ? userDetail.getFirstName() : "") + " " + 
                         (userDetail.getLastName() != null ? userDetail.getLastName() : "");
        
        return JwtResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .type("Bearer")
                .id(userDetail.getId())
                .userName(userDetail.getUserName())
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

    public void logout(String token) {
        try {
            if (tokenProvider.validateToken(token)) {
                tokenBlacklistService.blacklistToken(token);
                String username = tokenProvider.getUsernameFromToken(token);
            } else {
                throw new RuntimeException("Invalid token");
            }
        } catch (Exception e) {
            throw new RuntimeException("Logout failed: " + e.getMessage());
        }
    }

    public JwtResponse refreshToken(String refreshToken) {
        try {
            if (!tokenProvider.validateToken(refreshToken)) {
                throw new RuntimeException("Invalid refresh token");
            }

            if (!tokenProvider.isRefreshToken(refreshToken)) {
                throw new RuntimeException("Token is not a refresh token");
            }

            if (tokenBlacklistService.isTokenBlacklisted(refreshToken)) {
                throw new RuntimeException("Refresh token is blacklisted");
            }

            String username = tokenProvider.getUsernameFromToken(refreshToken);
            Long userId = tokenProvider.getUserIdFromToken(refreshToken);

            // Generate new tokens
            String newAccessToken = tokenProvider.generateToken(username, userId);
            String newRefreshToken = tokenProvider.generateRefreshToken(username, userId);

            // Blacklist old refresh token
            tokenBlacklistService.blacklistToken(refreshToken);

            // Get user details from cache or user service
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            
            return JwtResponse.builder()
                    .token(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .type("Bearer")
                    .id(userId)
                    .userName(username)
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Token refresh failed: " + e.getMessage());
        }
    }
}
