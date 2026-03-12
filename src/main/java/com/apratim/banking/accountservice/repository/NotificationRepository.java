package com.apratim.banking.accountservice.repository;

import com.apratim.banking.accountservice.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByAccountNumberOrderByCreatedAtDesc(String accountNumber);
}
