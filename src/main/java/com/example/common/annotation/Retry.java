package com.example.common.annotation;

import java.lang.annotation.*;

/**
 * Retry Annotation
 * Tự động retry khi method thất bại
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Retry {
    String name() default "";
    int maxRetries() default 3;
    long delayMillis() default 1000;
    Class<? extends Throwable>[] retryOn() default {Exception.class};
}
