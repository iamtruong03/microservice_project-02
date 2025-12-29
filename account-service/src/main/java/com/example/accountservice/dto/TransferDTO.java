package com.example.accountservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferDTO {
    private Long fromAccountId;
    private Long toAccountId;
    private BigDecimal amount;
    private String description;
}
