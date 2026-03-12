package com.apratim.banking.accountservice.controller;

import com.apratim.banking.accountservice.entity.Notification;
import com.apratim.banking.accountservice.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<List<Notification>> getNotifications(
            @PathVariable String accountNumber
    ) {
        return ResponseEntity.ok(
                notificationService.getNotificationsForAccount(accountNumber)
        );
    }
}
