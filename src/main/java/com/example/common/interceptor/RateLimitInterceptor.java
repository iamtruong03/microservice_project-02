package com.example.common.interceptor;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Rate Limiting Interceptor
 * Kiểm soát số lượng requests trên một khoảng thời gian
 */
@Slf4j
@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final RateLimiterRegistry rateLimiterRegistry;

    public RateLimitInterceptor(RateLimiterRegistry rateLimiterRegistry) {
        this.rateLimiterRegistry = rateLimiterRegistry;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String clientId = getClientId(request);
        
        RateLimiter rateLimiter = rateLimiterRegistry.rateLimiter(
            clientId,
            io.github.resilience4j.ratelimiter.RateLimiterConfig.custom()
                .limitRefreshPeriod(java.time.Duration.ofMinutes(1))
                .limitForPeriod(100)
                .build()
        );

        if (!rateLimiter.acquirePermission()) {
            log.warn("Rate limit exceeded for client: {}", clientId);
            response.setStatus(429); // Too Many Requests
            response.getWriter().write("Rate limit exceeded");
            return false;
        }

        return true;
    }

    private String getClientId(HttpServletRequest request) {
        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty()) {
            clientIp = request.getRemoteAddr();
        }
        return clientIp;
    }
}
