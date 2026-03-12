package com.apratim.banking.accountservice.dto;

import com.apratim.banking.accountservice.model.RecurringFrequency;

import java.math.BigDecimal;

public class CreateRecurringTransactionRequest {

    private String fromAccount;
    private String toAccount;
    private BigDecimal amount;
    private RecurringFrequency frequency;

    public CreateRecurringTransactionRequest(String fromAccount, String toAccount,
                                             BigDecimal amount, RecurringFrequency frequency) {
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
        this.frequency = frequency;
    }

    public String getFromAccount() {
        return fromAccount;
    }

    public void setFromAccount(String fromAccount) {
        this.fromAccount = fromAccount;
    }

    public String getToAccount() {
        return toAccount;
    }

    public void setToAccount(String toAccount) {
        this.toAccount = toAccount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public RecurringFrequency getFrequency() {
        return frequency;
    }

    public void setFrequency(RecurringFrequency frequency) {
        this.frequency = frequency;
    }
}
