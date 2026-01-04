package com.example.auditservice.service;

import com.example.auditservice.dto.AuditEvent;
import com.example.auditservice.mapper.AuditLogMapper;
import com.example.auditservice.repository.AuditLogRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditLogService {

  private final AuditLogRepository repository;
  private final AuditLogMapper mapper;

  @Transactional
  public void saveIfNotExists(AuditEvent event) {

    boolean exists = repository
        .existsByEntityTypeAndEntityIdAndActionAndReferenceCode(
            event.getEntityType(),
            event.getEntityId(),
            event.getAction(),
            event.getReferenceCode()
        );

    if (!exists) {
      repository.save(mapper.toEntity(event));
    }
  }
}

