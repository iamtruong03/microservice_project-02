package com.example.transactionservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionDTO(
    Long id,
    Long fromAccountId,
    Long toAccountId,
    BigDecimal amount,
    String currency,
    Long accountSequence,
    Long transactionTypeId,
    String state,
    String referenceCode,
    String description,
    String createdBy,
    LocalDateTime createdAt,
    LocalDateTime completedAt
) {}
