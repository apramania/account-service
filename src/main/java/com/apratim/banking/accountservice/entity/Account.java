package com.apratim.banking.accountservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "accounts")
@Getter
@Setter
public class Account {
    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", customerId='" + customerId + '\'' +
                ", balance=" + balance +
                ", status='" + status + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", version='" + version + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }

    @Version
    private Long version;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String customerId;

    @Column(nullable = false)
    private BigDecimal balance;

    @Column(nullable = false)
    private String status;

    @Column(unique = true, nullable = false)
    private String accountNumber;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist(){
        this.createdAt = LocalDateTime.now();
    }

    public Account() {
    }

    public Account(String customerId, BigDecimal balance, String status, String accountNumber) {
        this.customerId = customerId;
        this.balance = balance;
        this.status = status;
        this.accountNumber = accountNumber;
        //this.version = version;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
