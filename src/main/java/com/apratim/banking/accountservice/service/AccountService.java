package com.apratim.banking.accountservice.service;

import com.apratim.banking.accountservice.dto.AccountResponse;
import com.apratim.banking.accountservice.dto.CreateAccountRequest;
import com.apratim.banking.accountservice.entity.Account;
import com.apratim.banking.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public AccountResponse createAccount(CreateAccountRequest request){
        Account account = new Account(
                request.getCustomerId(),
                request.getInitialBalance() != null ? request.getInitialBalance() : BigDecimal.ZERO,
                "ACTIVE"
        );

        Account savedAccount = accountRepository.save(account);

        AccountResponse response = new AccountResponse();
        response.setId(savedAccount.getId());
        response.setCustomerId(savedAccount.getCustomerId());
        response.setBalance(savedAccount.getBalance());
        response.setStatus(savedAccount.getStatus());
        response.setCreatedAt(savedAccount.getCreatedAt());

        return response;
    }
}
