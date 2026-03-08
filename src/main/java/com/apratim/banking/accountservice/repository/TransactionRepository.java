package com.apratim.banking.accountservice.repository;

import com.apratim.banking.accountservice.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

}
