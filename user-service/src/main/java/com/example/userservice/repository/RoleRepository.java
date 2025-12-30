package com.example.userservice.repository;

import com.example.userservice.model.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
  Optional<Role> findByName(String name);
  boolean existsByName(String name);
  Page<Role> findByNameContainingIgnoreCase(String keyword, Pageable pageable);
}
