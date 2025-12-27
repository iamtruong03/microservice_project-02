package com.example.apigateway.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtTokenValidator {
    
    public boolean isValidBearerToken(String token) {
        if (token == null) {
            return false;
        }
        return token.startsWith("Bearer ") && token.length() > 7;
    }

    public String extractToken(String bearerToken) {
        if (isValidBearerToken(bearerToken)) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
