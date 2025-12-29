package com.example.accountservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepositWithdrawDTO {
    private Long accountId;
    private BigDecimal amount;
    private String type; // DEPOSIT, WITHDRAW
    private String description;
}
