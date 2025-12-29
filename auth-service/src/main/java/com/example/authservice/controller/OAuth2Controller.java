package com.example.authservice.controller;

import com.example.authservice.dto.JwtResponse;
import com.example.authservice.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/oauth2")
@RequiredArgsConstructor
@Slf4j
public class OAuth2Controller {

    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Endpoint to check OAuth2 authentication status
     * Called by frontend after OAuth2 redirect
     */
    @GetMapping("/user")
    public ResponseEntity<?> getOAuth2User(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof OAuth2User)) {
            return ResponseEntity.status(401).body("Not authenticated");
        }

        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        return ResponseEntity.ok(oauth2User.getAttributes());
    }

    /**
     * Get login URL for frontend
     */
    @GetMapping("/login-url")
    public ResponseEntity<String> getLoginUrl() {
        String loginUrl = "http://localhost:8086/oauth2/authorization/google";
        return ResponseEntity.ok(loginUrl);
    }

    /**
     * Verify token from OAuth2 flow
     */
    @GetMapping("/verify/{token}")
    public ResponseEntity<String> verifyToken(@PathVariable String token) {
        if (jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.ok("Token is valid");
        }
        return ResponseEntity.status(401).body("Invalid token");
    }
}
