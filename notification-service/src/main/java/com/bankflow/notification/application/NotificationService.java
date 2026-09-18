package com.bankflow.notification.application;

import com.bankflow.notification.domain.Notification;
import com.bankflow.notification.event.TransferCompletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final Logger log =
            LoggerFactory.getLogger(NotificationService.class);

    public void sendTransferNotification(
            TransferCompletedEvent event
    ) {

        Notification notification =
                new Notification(
                        "Virement effectué",
                        "Virement de "
                                + event.amount()
                                + " "
                                + event.currency()
                                + " effectué avec succès"
                );

        log.info(
                "Notification sent title={} message={} transactionId={}",
                notification.title(),
                notification.message(),
                event.transactionId()
        );
    }
}