package com.apratim.banking.accountservice.repository;

import com.apratim.banking.accountservice.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Page<Transaction> findByFromAccount_AccountNumberOrToAccount_AccountNumber(
            String fromAccountNumber,
            String toAccountNumber,
            Pageable pageable
    );

    @Query("""
    SELECT COALESCE(SUM(t.amount),0)
    FROM Transaction t
    WHERE t.fromAccount.accountNumber = :accountNumber
    AND t.createdAt >= :startOfDay
    AND t.createdAt < :endOfDay
    """)
    BigDecimal getTodayTransferTotal(
            String accountNumber,
            LocalDateTime startOfDay,
            LocalDateTime endOfDay
    );

}
