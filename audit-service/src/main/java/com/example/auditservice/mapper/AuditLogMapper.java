package com.example.auditservice.mapper;

import com.example.auditservice.dto.AuditEvent;
import com.example.auditservice.entity.AuditLog;
import org.springframework.stereotype.Component;

@Component
public class AuditLogMapper {

  public AuditLog toEntity(AuditEvent event) {
    AuditLog log = new AuditLog();
    log.setEntityType(event.getEntityType());
    log.setEntityId(event.getEntityId());
    log.setAction(event.getAction());
    log.setStateBefore(event.getStateBefore());
    log.setStateAfter(event.getStateAfter());
    log.setAmount(event.getAmount());
    log.setBalanceBefore(event.getBalanceBefore());
    log.setBalanceAfter(event.getBalanceAfter());
    log.setReferenceCode(event.getReferenceCode());
    log.setIdempotencyKey(event.getIdempotencyKey());
    log.setActorType(event.getActorType());
    log.setActorId(event.getActorId());
    log.setSourceService(event.getSourceService());
    log.setSourceIp(event.getSourceIp());
    log.setUserAgent(event.getUserAgent());
    log.setCreatedAt(event.getCreatedAt());
    return log;
  }
}

