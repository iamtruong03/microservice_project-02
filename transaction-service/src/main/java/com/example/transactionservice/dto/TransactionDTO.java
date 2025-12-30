package com.example.transactionservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionDTO(
    Long id,
    Long fromAccountId,
    Long toAccountId,
    BigDecimal amount,
    String currency,
    String transactionType,
    String status,
    String referenceCode,
    String description,
    LocalDateTime createdAt,
    LocalDateTime completedAt
) {}
