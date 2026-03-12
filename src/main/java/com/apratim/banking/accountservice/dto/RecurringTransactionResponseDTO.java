package com.apratim.banking.accountservice.dto;

import java.math.BigDecimal;

public record RecurringTransactionResponseDTO(
        Long id,
        String fromAccount,
        String toAccount,
        BigDecimal amount
       ) {
}
