package com.example.userservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name", nullable = false, unique = true)
  private String name;

  @Column(name = "description")
  private String description;

  // Store permission IDs as comma-separated string for distributed architecture
  // e.g., "1,2,3" instead of using @ManyToMany relationship
  @Column(name = "permission_ids", columnDefinition = "TEXT")
  @Builder.Default
  private String permissionIds = ""; // Store permission IDs as string

  @Column(name = "is_active", nullable = false)
  @Builder.Default
  private Boolean isActive = true;
}
