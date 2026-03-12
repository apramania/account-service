package com.apratim.banking.accountservice.exception;

import java.math.BigDecimal;

public class TransferLimitExceedException extends RuntimeException{

    public TransferLimitExceedException(String transactionTotal) {
        super("Daily transfer limit exceeded. Your total transaction for the day is : " +transactionTotal);
    }
}
