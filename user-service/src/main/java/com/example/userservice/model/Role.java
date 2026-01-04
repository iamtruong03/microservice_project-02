package com.example.userservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.type.SqlTypes;
import org.hibernate.annotations.JdbcTypeCode;
import java.util.List;

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

  @Column(name = "is_active", nullable = false)
  @Builder.Default
  private Boolean isActive = true;

  @Column(name = "created_at", nullable = false)
  @Builder.Default
  private java.time.LocalDateTime createdAt = java.time.LocalDateTime.now();

  @Column(name = "updated_at")
  private java.time.LocalDateTime updatedAt;

  @Column(name = "created_by")
  private String createdBy;

  @Column(name = "updated_by")
  private String updatedBy;
}
