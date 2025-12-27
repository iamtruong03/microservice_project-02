package com.example.common.aspect;

import com.example.common.annotation.ValidateUser;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * AOP Aspect cho validation user authority
 */
@Aspect
@Component
@Slf4j
public class ValidationAspect {

    @Before("@annotation(validateUser)")
    public void validateUserAccess(JoinPoint joinPoint, ValidateUser validateUser) {
        String[] allowedRoles = validateUser.roles();
        
        // Lấy current user từ SecurityContext (mock)
        String currentUserRole = "USER"; // Sẽ lấy từ SecurityContextHolder thực tế
        
        boolean hasAccess = Arrays.asList(allowedRoles).contains(currentUserRole);
        
        if (!hasAccess) {
            log.warn("❌ UNAUTHORIZED ACCESS: User role {} not in allowed roles {}", 
                    currentUserRole, Arrays.toString(allowedRoles));
            throw new RuntimeException("User is not authorized to access this resource");
        }
        
        log.info("✅ USER AUTHORIZED: User role {} has access", currentUserRole);
    }
}
