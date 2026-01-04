package com.example.auditservice.consumer;

import com.example.auditservice.dto.AuditEvent;
import com.example.auditservice.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuditEventConsumer {

  private final AuditLogService auditLogService;

  @KafkaListener(
      topics = "audit-events",
      groupId = "audit-service"
  )
  public void consume(AuditEvent event) {
    auditLogService.saveIfNotExists(event);
  }
}

