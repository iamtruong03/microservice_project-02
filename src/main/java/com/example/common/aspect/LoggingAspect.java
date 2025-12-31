package com.example.common.aspect;

import com.example.common.annotation.LogExecutionTime;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * AOP Aspect cho logging
 * Tự động log tất cả method calls được annotate bằng @LogExecutionTime
 */
@Aspect
@Component
@Slf4j
public class LoggingAspect {

    /**
     * Before advice - log trước khi method được thực thi
     */
    @Before("@annotation(logExecution)")
    public void logBefore(JoinPoint joinPoint, LogExecutionTime logExecution) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        Object[] args = joinPoint.getArgs();

    }

    /**
     * After Returning advice - log sau khi method execute thành công
     */
    @AfterReturning(value = "@annotation(logExecution)", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, LogExecutionTime logExecution, Object result) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

    }

    /**
     * After Throwing advice - log khi method throw exception
     */
    @AfterThrowing(value = "@annotation(logExecution)", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, LogExecutionTime logExecution, Throwable exception) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

    }

    /**
     * Around advice - log execution time
     */
    @Around("@annotation(logExecution)")
    public Object logExecutionTime(org.aspectj.lang.ProceedingJoinPoint pjp, LogExecutionTime logExecution) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        try {
            Object result = pjp.proceed();
            long duration = System.currentTimeMillis() - startTime;
            
            String methodName = pjp.getSignature().getName();
            String className = pjp.getTarget().getClass().getSimpleName();
            
            
            return result;
        } catch (Throwable ex) {
            long duration = System.currentTimeMillis() - startTime;
            String methodName = pjp.getSignature().getName();
            String className = pjp.getTarget().getClass().getSimpleName();
            
            throw ex;
        }
    }
}
