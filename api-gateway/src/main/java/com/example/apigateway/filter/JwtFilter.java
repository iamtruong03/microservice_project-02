package com.example.apigateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import javax.crypto.SecretKey;

@Slf4j
@Component
public class JwtFilter extends AbstractGatewayFilterFactory<JwtFilter.Config> {

    @Value("${app.jwtSecret:9mVL3xK8pQ2wE5rT7yU1oI4aS6dF0gH3jK5lM7nO9pQ1wE3rT5yU7}")
    private String jwtSecret;

    public JwtFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
            
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            try {
                String token = authHeader.substring(7);
                String username = extractUsernameFromToken(token);
                Long userId = extractUserIdFromToken(token);
                
                // Thêm uid vào header cho các service
                exchange.getRequest().mutate()
                        .header("uid", userId != null ? userId.toString() : username)
                        .build();
                
                return chain.filter(exchange);
            } catch (Exception e) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        };
    }

    private String extractUsernameFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    private Long extractUserIdFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        Object uid = claims.get("uid");
        if (uid == null) {
            return null;
        }
        if (uid instanceof Long) {
            return (Long) uid;
        }
        if (uid instanceof Integer) {
            return ((Integer) uid).longValue();
        }
        return Long.parseLong(uid.toString());
    }

    public static class Config {
    }
}
