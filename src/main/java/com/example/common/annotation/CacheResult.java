package com.example.common.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * Cache Result Annotation
 * Tự động cache result của method
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CacheResult {
    String key() default "";
    long ttl() default 3600;
    TimeUnit timeUnit() default TimeUnit.SECONDS;
    boolean clearOnUpdate() default false;
}
