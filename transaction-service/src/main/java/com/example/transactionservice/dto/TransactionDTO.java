package com.example.transactionservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionDTO(
    Long id,
    Long fromUserId,
    Long toUserId,
    Long fromAccountId,
    Long toAccountId,
    BigDecimal amount,
    String status,
    String description,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
