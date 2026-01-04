package com.example.userservice.repository;

import com.example.userservice.model.RolePermission;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolePermissionRepository
    extends JpaRepository<RolePermission, Long> {

  void deleteByRoleId(Long roleId);

  List<RolePermission> findByRoleId(Long roleId);
}

