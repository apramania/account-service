package com.apratim.banking.accountservice.service;

import com.apratim.banking.accountservice.exception.TransferLimitExceedException;
import com.apratim.banking.accountservice.model.AccountStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class AccountService {

    private static final BigDecimal DAILY_TRANSFER_LIMIT = new BigDecimal("10000");
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

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
                AccountStatus.ACTIVE,
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

        logger.info("Transfer initiated: from {} to {} amount {}",
                request.getFromAccountNumber(),
                request.getToAccountNumber(),
                request.getAmount());

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

        logger.debug("Fetched account: fromAccount = {}, toAccount = {}",
                fromAccount.getAccountNumber(),
                toAccount.getAccountNumber());

        if(fromAccount.getStatus() != AccountStatus.ACTIVE){
            throw new RuntimeException("Source account is frozen");
        }

        if(toAccount.getStatus() != AccountStatus.ACTIVE){
            throw new RuntimeException("Destination account is frozen");
        }

        if(request.getFromAccountNumber().equals(request.getToAccountNumber())){
            throw new IllegalArgumentException("Cannot transfer to same account");
        }

        if(fromAccount.getBalance().compareTo(request.getAmount()) < 0){
            throw new InsufficientBalanceException(fromAccount.getAccountNumber());
        }

        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        BigDecimal todayTransferred = transactionRepository
                .getTodayTransferTotal(
                        fromAccount.getAccountNumber(),
                        startOfDay,
                        endOfDay
                        );

        BigDecimal newTotal = todayTransferred.add(request.getAmount());

        if (newTotal.compareTo(DAILY_TRANSFER_LIMIT) > 0){
            throw new TransferLimitExceedException(newTotal.toString());
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
        TransactionResponse transferDetails = transactionService
                .transferDetails(request,fromAccount,toAccount, "SUCCESS");

        logger.info("Transfer successful: from {} to {} amount {}",
                request.getFromAccountNumber(),
                request.getToAccountNumber(),
                request.getAmount());

        return transferDetails;
    }


    // utility method to generate account number
    private String generateAccountNumber(){
        return String.valueOf(System.currentTimeMillis());
    }

    // freeze account impl
    public AccountResponse freezeAccount(String accountNumber){
        Account account = accountRepository.findAccountByAccountNumber(accountNumber)
                .orElseThrow(() ->  new AccountNotFoundException(accountNumber));

        account.setStatus(AccountStatus.FROZEN);

        Account savedAccount = accountRepository.save(account);

        AccountResponse response = new AccountResponse();
        response.setId(savedAccount.getId());
        response.setCustomerId(savedAccount.getCustomerId());
        response.setBalance(savedAccount.getBalance());
        response.setStatus(savedAccount.getStatus());
        response.setAccountNumber(savedAccount.getAccountNumber());
        response.setCreatedAt(savedAccount.getCreatedAt());

        return  response;
    }

    // unfreeze account impl
    public AccountResponse unfreezeAccount(String accountNumber){
        Account account = accountRepository.findAccountByAccountNumber(accountNumber)
                .orElseThrow(() ->  new AccountNotFoundException(accountNumber));

        account.setStatus(AccountStatus.ACTIVE);

        Account savedAccount = accountRepository.save(account);

        AccountResponse response = new AccountResponse();
        response.setId(savedAccount.getId());
        response.setCustomerId(savedAccount.getCustomerId());
        response.setBalance(savedAccount.getBalance());
        response.setStatus(savedAccount.getStatus());
        response.setAccountNumber(savedAccount.getAccountNumber());
        response.setCreatedAt(savedAccount.getCreatedAt());

        return  response;
    }
}
