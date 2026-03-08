package com.apratim.banking.accountservice.service;

import com.apratim.banking.accountservice.dto.TransactionResponse;
import com.apratim.banking.accountservice.dto.TransferRequest;
import com.apratim.banking.accountservice.entity.Account;
import com.apratim.banking.accountservice.entity.Transaction;
import com.apratim.banking.accountservice.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class TransactionService {


    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public TransactionResponse transferDetails(TransferRequest request, Account fromAccount, Account toAccount,
                                               String status){
        Transaction transaction = new Transaction();

        transaction.setTransactionReference(generateTransactionReference());
        transaction.setFromAccount(fromAccount);
        transaction.setToAccount(toAccount);
        transaction.setAmount(request.getAmount());
        transaction.setStatus(status);
        transaction.setCreatedAt(LocalDateTime.now());

        Transaction savedTransaction = transactionRepository.save(transaction);

        return mapToResponse(savedTransaction);
    }

    private String generateTransactionReference() {
        return "TXN-" + System.currentTimeMillis();
    }

    private TransactionResponse mapToResponse(Transaction transaction){
        TransactionResponse transactionResponse = new TransactionResponse();
        transactionResponse.setTransactionReference(generateTransactionReference());
        transactionResponse.setFromAccountNumber(transaction.getFromAccount().getAccountNumber());
        transactionResponse.setToAccountNumber(transaction.getToAccount().getAccountNumber());
        transactionResponse.setAmount(transaction.getAmount());
        transactionResponse.setStatus("SUCCESS");
        transactionResponse.setCreatedAt(LocalDateTime.now());

        return transactionResponse;
    }
}
