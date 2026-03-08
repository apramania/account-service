package com.apratim.banking.accountservice.controller;

import com.apratim.banking.accountservice.dto.AccountResponse;
import com.apratim.banking.accountservice.dto.CreateAccountRequest;
import com.apratim.banking.accountservice.dto.TransactionResponse;
import com.apratim.banking.accountservice.dto.TransferRequest;
import com.apratim.banking.accountservice.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
            @RequestBody CreateAccountRequest request
            ){
        AccountResponse response = accountService.createAccount(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable String accountNumber){
        return ResponseEntity.ok(accountService.getAccountByAccountNumber(accountNumber));
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> transferMoney(
            @Valid @RequestBody TransferRequest request
            ){
        return ResponseEntity.ok(accountService.transferMoney(request));
    }
}
