package com.apratim.banking.accountservice.controller;


import com.apratim.banking.accountservice.dto.CreateRecurringTransactionRequest;
import com.apratim.banking.accountservice.dto.RecurringTransactionResponseDTO;
import com.apratim.banking.accountservice.entity.RecurringTransaction;
import com.apratim.banking.accountservice.service.RecurringTransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recurring-transactions")
public class RecurringTransactionController {

    private final RecurringTransactionService recurringTransactionService;

    public RecurringTransactionController(RecurringTransactionService recurringTransactionService) {
        this.recurringTransactionService = recurringTransactionService;
    }

    @PostMapping
    public ResponseEntity<RecurringTransaction> createRecurringTransaction(
            @RequestBody CreateRecurringTransactionRequest request
            ){
        return ResponseEntity.ok(
                recurringTransactionService.createRecurringTransaction(request)
        );
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<List<RecurringTransactionResponseDTO>> getRecurringTransaction(
            @PathVariable String accountNumber
    ) {
        return ResponseEntity.ok(
                recurringTransactionService.getRecurringTransactionsForAccount(accountNumber)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelRecurringTransaction(
            @PathVariable Long id
    ){
        recurringTransactionService.cancelRecurringTransaction(id);
        return ResponseEntity.noContent().build();
    }
}
