package com.example.auditservice.repository;

import com.example.auditservice.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

  boolean existsByEntityTypeAndEntityIdAndActionAndReferenceCode(
      String entityType,
      Long entityId,
      String action,
      String referenceCode
  );
}

