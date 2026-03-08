package com.apratim.banking.accountservice.service;

import com.apratim.banking.accountservice.dto.AccountResponse;
import com.apratim.banking.accountservice.dto.CreateAccountRequest;
import com.apratim.banking.accountservice.dto.TransactionResponse;
import com.apratim.banking.accountservice.dto.TransferRequest;
import com.apratim.banking.accountservice.entity.Account;
import com.apratim.banking.accountservice.entity.Transaction;
import com.apratim.banking.accountservice.exception.AccountNotFoundException;
import com.apratim.banking.accountservice.exception.InsufficientBalanceException;
import com.apratim.banking.accountservice.repository.AccountRepository;
import com.apratim.banking.accountservice.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    private final TransactionService transactionService;

    public AccountService(AccountRepository accountRepository, TransactionRepository transactionRepository,
                          TransactionService transactionService) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.transactionService = transactionService;
    }

    public AccountResponse createAccount(CreateAccountRequest request){
        Account account = new Account(
                request.getCustomerId(),
                request.getInitialBalance() != null ? request.getInitialBalance() : BigDecimal.ZERO,
                "ACTIVE",
                generateAccountNumber()
        );

        Account savedAccount = accountRepository.save(account);

        AccountResponse response = new AccountResponse();
        response.setId(savedAccount.getId());
        response.setCustomerId(savedAccount.getCustomerId());
        response.setBalance(savedAccount.getBalance());
        response.setStatus(savedAccount.getStatus());
        response.setAccountNumber(savedAccount.getAccountNumber());
        response.setCreatedAt(savedAccount.getCreatedAt());

        return response;
    }

    public AccountResponse getAccountByAccountNumber(String accountNumber){
        Account account = accountRepository.findAccountByAccountNumber(accountNumber)
                .orElseThrow(() ->  new AccountNotFoundException(accountNumber));


        return new AccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getBalance(),
                account.getStatus(),
                account.getCustomerId(),
                account.getCreatedAt()
        );
    }

    @Transactional
    public TransactionResponse transferMoney(TransferRequest request){

        Transaction transaction = new Transaction();

        Account fromAccount = accountRepository
                .findAccountByAccountNumber(request.getFromAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException(
                        request.getFromAccountNumber()
                ));

        Account toAccount = accountRepository
                .findAccountByAccountNumber(request.getToAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException(
                        request.getToAccountNumber()
                ));

        if(request.getFromAccountNumber().equals(request.getToAccountNumber())){
            throw new IllegalArgumentException("Cannot transfer to same account");
        }

        if(fromAccount.getBalance().compareTo(request.getAmount()) < 0){
            throw new InsufficientBalanceException(fromAccount.getAccountNumber());
        }

        fromAccount.setBalance(
                fromAccount.getBalance().subtract(request.getAmount())
        );

        toAccount.setBalance(
                toAccount.getBalance().add(request.getAmount())
        );

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        // call to transaction service
        return transactionService.transferDetails(request,fromAccount,toAccount, "SUCCESS");
    }


    // utility method to generate account number
    private String generateAccountNumber(){
        return String.valueOf(System.currentTimeMillis());
    }

}
