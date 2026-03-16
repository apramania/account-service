package com.apratim.banking.accountservice.listener;

import com.apratim.banking.accountservice.event.TransferCompletedEvent;
import com.apratim.banking.accountservice.service.NotificationService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class TransferEventListener {

    private final NotificationService notificationService;

    public TransferEventListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }


    // This method will be called when a TransferCompletedEvent is published
    @EventListener
    public void onTransferCompleted(TransferCompletedEvent event) {

        System.out.println("EVENT RECEIVED");
        // Create notifications for both accounts involved in the transfer
        String fromMessage = String.format("You transferred %s to account %s",
                event.getAmount(), event.getToAccount());
        String toMessage = String.format("You received %s from account %s",
                event.getAmount(), event.getFromAccount());

        notificationService.sendNotification(
                event.getFromAccount(),
                fromMessage,
                "TRANSFER");
        notificationService.sendNotification(
                event.getToAccount(),
                toMessage,
                "TRANSFER");
    }
}
