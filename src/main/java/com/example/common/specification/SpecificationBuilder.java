package com.example.common.specification;

import org.springframework.data.jpa.domain.Specification;

/**
 * Generic Specification Builder
 * Dùng để build dynamic queries với Specifications
 */
public class SpecificationBuilder<T> {
    
    /**
     * Build specification cho tìm kiếm by field = value
     */
    public static <T> Specification<T> equal(String field, Object value) {
        return (root, query, cb) -> {
            if (value == null) return cb.conjunction();
            return cb.equal(root.get(field), value);
        };
    }

    /**
     * Build specification cho tìm kiếm LIKE
     */
    public static <T> Specification<T> like(String field, String value) {
        return (root, query, cb) -> {
            if (value == null || value.isEmpty()) return cb.conjunction();
            return cb.like(cb.lower(root.get(field)), "%" + value.toLowerCase() + "%");
        };
    }

    /**
     * Build specification cho tìm kiếm > (greater than)
     */
    public static <T> Specification<T> greaterThan(String field, Comparable value) {
        return (root, query, cb) -> {
            if (value == null) return cb.conjunction();
            return cb.greaterThan(root.get(field), value);
        };
    }

    /**
     * Build specification cho tìm kiếm < (less than)
     */
    public static <T> Specification<T> lessThan(String field, Comparable value) {
        return (root, query, cb) -> {
            if (value == null) return cb.conjunction();
            return cb.lessThan(root.get(field), value);
        };
    }

    /**
     * Build specification cho range (between)
     */
    public static <T> Specification<T> between(String field, Comparable start, Comparable end) {
        return (root, query, cb) -> {
            if (start == null || end == null) return cb.conjunction();
            return cb.between(root.get(field), start, end);
        };
    }

    /**
     * Combine hai specifications với AND
     */
    public static <T> Specification<T> and(Specification<T> spec1, Specification<T> spec2) {
        return (root, query, cb) -> cb.and(
            spec1.toPredicate(root, query, cb),
            spec2.toPredicate(root, query, cb)
        );
    }

    /**
     * Combine hai specifications với OR
     */
    public static <T> Specification<T> or(Specification<T> spec1, Specification<T> spec2) {
        return (root, query, cb) -> cb.or(
            spec1.toPredicate(root, query, cb),
            spec2.toPredicate(root, query, cb)
        );
    }
}
