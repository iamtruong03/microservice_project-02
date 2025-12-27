package com.example.common.annotation;

import java.lang.annotation.*;

/**
 * AOP Logging Annotation
 * Tự động log method calls, arguments, return values, execution time
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogExecutionTime {
    String value() default "Method execution";
}
