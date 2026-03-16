package com.apratim.banking.accountservice.dto;


import java.math.BigDecimal;


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
