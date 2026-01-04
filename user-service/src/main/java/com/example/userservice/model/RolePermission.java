package com.example.userservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "role_permissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RolePermission {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "role_id", nullable = false)
  private Long roleId;

  @Column(name = "permission_id", nullable = false)
  private Long permissionId;

  @Column(name = "created_at", nullable = false)
  private java.time.LocalDateTime createdAt = java.time.LocalDateTime.now();

  @Column(name = "updated_at")
  private java.time.LocalDateTime updatedAt;

  @Column(name = "created_by")
  private String createdBy;

  @Column(name = "updated_by")
  private String updatedBy;
}

