package com.example.common.annotation;

import java.lang.annotation.*;

/**
 * Validate User Authority Annotation
 * Kiểm tra user có quyền truy cập method này không
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidateUser {
    String[] roles() default {"USER", "ADMIN"};
}
