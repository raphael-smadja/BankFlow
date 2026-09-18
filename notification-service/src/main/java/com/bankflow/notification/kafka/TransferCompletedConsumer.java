package com.bankflow.notification.kafka;

import com.bankflow.notification.application.NotificationService;
import com.bankflow.notification.event.TransferCompletedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.core.JacksonException;

@Component
public class TransferCompletedConsumer {

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    public TransferCompletedConsumer(
            NotificationService notificationService,
            ObjectMapper objectMapper
    ) {
        this.notificationService = notificationService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(
            topics = "bankflow.transfer.completed.v1",
            groupId = "notification-service"
    )
    public void consume(String payload) {

        try {

            TransferCompletedEvent event =
                    objectMapper.readValue(
                            payload,
                            TransferCompletedEvent.class
                    );

            notificationService
                    .sendTransferNotification(event);

        } catch (JacksonException exception) {
            throw new IllegalStateException(
                    "Unable to deserialize TransferCompletedEvent",
                    exception
            );
        }
    }
}