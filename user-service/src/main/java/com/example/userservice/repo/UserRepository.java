package com.example.userservice.repo;

import com.example.userservice.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {
  boolean existsByEmail(String email);
  boolean existsByEmailAndIdNot(String email, Long id);
  
  // Tìm kiếm theo tên hoặc email
  @Query("SELECT u FROM User u WHERE " +
         "(:keyword IS NULL OR :keyword = '' OR " +
         "LOWER(u.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
         "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')))")
  Page<User> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
  
  // Tìm kiếm theo tên
  Page<User> findByNameContainingIgnoreCase(String name, Pageable pageable);
  
  // Tìm kiếm theo email
  Page<User> findByEmailContainingIgnoreCase(String email, Pageable pageable);
}


