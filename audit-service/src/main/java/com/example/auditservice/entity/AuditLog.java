package com.example.auditservice.entity;

import java.math.BigDecimal;
import java.time.Instant;
import lombok.*;

@Entity
@Table(name = "audit_logs")
@Getter @Setter
public class AuditLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String entityType;
  private Long entityId;

  private String action;
  private String stateBefore;
  private String stateAfter;

  private BigDecimal amount;
  private BigDecimal balanceBefore;
  private BigDecimal balanceAfter;

  private String referenceCode;
  private String idempotencyKey;

  private String actorType;
  private Long actorId;

  private String sourceService;
  private String sourceIp;
  private String userAgent;

  private Instant createdAt;
}

