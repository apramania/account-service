package com.apratim.banking.accountservice.service;

import com.apratim.banking.accountservice.entity.Notification;
import com.apratim.banking.accountservice.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public void sendNotification(String accountNumber, String message, String type) {
        // Create and save a new notification

        System.out.println("Sending notification to account " + accountNumber + ": " + message);
        Notification notification = new Notification();


        notification.setType(type);
        notification.setAccountNumber(accountNumber);
        notification.setMessage(message);
        notification.setCreatedAt(java.time.LocalDateTime.now());

        notificationRepository.save(notification);
    }

    public java.util.List<Notification> getNotificationsForAccount(String accountNumber) {
        return notificationRepository.findByAccountNumberOrderByCreatedAtDesc(accountNumber);
    }
}
