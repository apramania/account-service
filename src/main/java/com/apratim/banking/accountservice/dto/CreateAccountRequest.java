package com.apratim.banking.accountservice.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateAccountRequest {
    private String customerId;
    private BigDecimal initialBalance;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public BigDecimal getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(BigDecimal initialBalance) {
        this.initialBalance = initialBalance;
    }
}
