package com.example.authservice.security;

import com.example.authservice.domain.User;
import com.example.authservice.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
@AllArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                       Authentication authentication) throws IOException, ServletException {
        try {
            OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
            
            // Extract user info from OAuth2User
            String email = oauth2User.getAttribute("email");
            String name = oauth2User.getAttribute("name");
            
            log.info("OAuth2 Login successful for email: {}", email);
            
            // Check if user exists in database
            Optional<User> existingUser = userRepository.findByEmail(email);
            
            User user;
            if (existingUser.isPresent()) {
                user = existingUser.get();
                // Update user if needed
                user.setFullName(name);
                user.setUpdatedAt(LocalDateTime.now().toString());
                userRepository.save(user);
            } else {
                // Create new user from OAuth2 credentials
                user = new User();
                user.setEmail(email);
                user.setUsername(email.split("@")[0]); // Use email prefix as username
                user.setFullName(name);
                user.setPassword(""); // OAuth2 users don't have password
                user.setEnabled(true);
                user.setCreatedAt(LocalDateTime.now().toString());
                user.setUpdatedAt(LocalDateTime.now().toString());
                userRepository.save(user);
                
                // Publish user registered event to Kafka
                publishUserRegisteredEvent(user);
            }
            
            // Generate JWT token using username
            String token = jwtTokenProvider.generateToken(user.getUsername());
            
            // Redirect to frontend with token
            String redirectUrl = "http://localhost:3000/login?token=" + token + "&email=" + email;
            response.sendRedirect(redirectUrl);
            
        } catch (Exception e) {
            log.error("Error during OAuth2 authentication: ", e);
            response.sendRedirect("http://localhost:3000/login?error=authentication_failed");
        }
    }

    private void publishUserRegisteredEvent(User user) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("eventType", "USER_REGISTERED");
            event.put("userId", user.getId());
            event.put("username", user.getUsername());
            event.put("email", user.getEmail());
            event.put("fullName", user.getFullName());
            event.put("timestamp", System.currentTimeMillis());

            kafkaTemplate.send("user-events", "user_registered", event);
            log.info("Published USER_REGISTERED event for OAuth2 user: {}", user.getUsername());
        } catch (Exception e) {
            log.error("Failed to publish user registered event", e);
        }
    }
}
