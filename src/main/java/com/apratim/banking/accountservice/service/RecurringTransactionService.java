package com.apratim.banking.accountservice.service;

import com.apratim.banking.accountservice.dto.CreateRecurringTransactionRequest;
import com.apratim.banking.accountservice.dto.RecurringTransactionResponseDTO;
import com.apratim.banking.accountservice.dto.TransferRequest;
import com.apratim.banking.accountservice.entity.Account;
import com.apratim.banking.accountservice.entity.RecurringTransaction;
import com.apratim.banking.accountservice.exception.AccountNotFoundException;
import com.apratim.banking.accountservice.model.RecurringFrequency;
import com.apratim.banking.accountservice.repository.AccountRepository;
import com.apratim.banking.accountservice.repository.RecurringTransactionRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class RecurringTransactionService {
    private final RecurringTransactionRepository recurringTransactionRepository;
    private final AccountRepository accountRepository;
    private final AccountService accountService;
    private final NotificationService notificationService;


    public RecurringTransactionService(RecurringTransactionRepository recurringTransactionRepository,
                                       AccountRepository accountRepository, AccountService accountService, NotificationService notificationService) {
        this.recurringTransactionRepository = recurringTransactionRepository;
        this.accountRepository = accountRepository;
        this.accountService = accountService;
        this.notificationService = notificationService;
    }

    public RecurringTransaction createRecurringTransaction(
            CreateRecurringTransactionRequest request
    ){
        Account fromAccount = accountRepository
                .findAccountByAccountNumber(request.getFromAccount())
                .orElseThrow(() -> new AccountNotFoundException(request.getFromAccount()));

        Account toAccount = accountRepository
                .findAccountByAccountNumber(request.getToAccount())
                .orElseThrow(() -> new AccountNotFoundException(request.getToAccount()));

        RecurringTransaction recurringTransaction = new RecurringTransaction();

        recurringTransaction.setFromAccount(fromAccount);
        recurringTransaction.setToAccount(toAccount);
        recurringTransaction.setAmount(request.getAmount());
        recurringTransaction.setFrequency(request.getFrequency());
        recurringTransaction.setNextExecutionDate(calculateNextExecutionDate(request.getFrequency()));
        recurringTransaction.setStatus("ACTIVE");

        return recurringTransactionRepository.save(recurringTransaction);
    }

    @Scheduled(fixedRate = 1000000)
    public void executeDueRecurringTransactions(){
        List<RecurringTransaction> dueTransactions =
                recurringTransactionRepository.findByNextExecutionDateBeforeAndStatus(
                        LocalDateTime.now(),
                        "ACTIVE"
                );

        for (RecurringTransaction recurring: dueTransactions){
            try{

                TransferRequest transferRequest = new TransferRequest();
                transferRequest.setFromAccountNumber(recurring.getFromAccount().getAccountNumber());
                transferRequest.setToAccountNumber(recurring.getToAccount().getAccountNumber());
                transferRequest.setAmount(recurring.getAmount());


                String idempotencyKey = UUID.randomUUID().toString();
                accountService.transferMoney(transferRequest, idempotencyKey);

                recurring.setNextExecutionDate(
                        calculateNextExecutionDate(recurring.getFrequency())
                );

                recurringTransactionRepository.save(recurring);

                notificationService.sendNotification(
                        recurring.getFromAccount().getAccountNumber(),
                        "Recurring transfer of " + recurring.getAmount() +
                                " to account " + recurring.getToAccount().getAccountNumber() +
                                " executed successfully.",
                        "RECURRING"
                );
            }catch (Exception e){
                System.out.println("Recurring transfer failed: " + e.getMessage());
            }
        }
    }

    private LocalDateTime calculateNextExecutionDate(RecurringFrequency frequency){
        LocalDateTime now = LocalDateTime.now();

        switch (frequency){

            case DAILY:
                return now.plusDays(1);

            case WEEKLY:
                return now.plusWeeks(1);

            case MONTHLY:
                return now.plusMonths(1);

            default:
                throw new IllegalArgumentException("Invalid Frequency");
        }
    }

    public void  cancelRecurringTransaction(Long id){
        RecurringTransaction recurringTransaction = recurringTransactionRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Recurring transaction not found"));

        recurringTransaction.setStatus("CANCELLED");
        recurringTransactionRepository.save(recurringTransaction);
    }

    public List<RecurringTransactionResponseDTO> getRecurringTransactionsForAccount(String accountNumber){
        List<RecurringTransaction> transactions = recurringTransactionRepository
                .findByFromAccount_AccountNumberOrToAccount_AccountNumber(accountNumber, accountNumber);

        return transactions.stream().map(
                t -> new RecurringTransactionResponseDTO(
                        t.getId(),
                        t.getFromAccount().getAccountNumber(),
                        t.getToAccount().getAccountNumber(),
                        t.getAmount()
                )
        ).toList();
    }


}
