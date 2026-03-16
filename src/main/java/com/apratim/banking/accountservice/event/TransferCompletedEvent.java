package com.apratim.banking.accountservice.event;

import java.math.BigDecimal;

public class TransferCompletedEvent {

    private String fromAccount;
    private String toAccount;
    private BigDecimal amount;

    public TransferCompletedEvent() {
    }

    public TransferCompletedEvent(String fromAccount, String toAccount, BigDecimal amount) {
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
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
}
