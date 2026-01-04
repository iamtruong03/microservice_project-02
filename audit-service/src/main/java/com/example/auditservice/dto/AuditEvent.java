package com.example.auditservice.dto;

import java.math.BigDecimal;
import java.time.Instant;
import lombok.Data;

@Data
public class AuditEvent {

  private String eventId;

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

