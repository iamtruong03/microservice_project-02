package com.example.common.annotation;

import java.lang.annotation.*;

/**
 * Rate Limit Annotation
 * Giới hạn số request được phép
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {
    String name() default "";
    int limitPerMinute() default 60;
    String fallback() default "";
}
