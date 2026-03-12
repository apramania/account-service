package com.apratim.banking.accountservice.repository;

import com.apratim.banking.accountservice.entity.RecurringTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface RecurringTransactionRepository extends JpaRepository<RecurringTransaction, Long> {
    List<RecurringTransaction> findByNextExecutionDateBeforeAndStatus(
            LocalDateTime time,
            String status
    );

    List<RecurringTransaction> findByFromAccount_AccountNumberOrToAccount_AccountNumber(String accountNumber,
                                                                                        String accountNumber1);
}
