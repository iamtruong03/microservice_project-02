package com.example.userservice.repo;

import com.example.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
  boolean existsByEmail(String email);
  boolean existsByEmailAndIdNot(String email, Long id);
}


