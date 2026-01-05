package com.example.accountservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountStatisticsDTO {
    private String userId;
    private BigDecimal totalBalance;
    private Long totalAccounts;
    private Long totalTransactions;
}
