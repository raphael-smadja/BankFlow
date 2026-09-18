package com.bankflow.account.infrastructure.kafka;

import com.bankflow.account.domain.OutboxEvent;
import com.bankflow.account.domain.OutboxStatus;
import com.bankflow.account.infrastructure.OutboxEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OutboxPublisher {

    private static final Logger log =
            LoggerFactory.getLogger(OutboxPublisher.class);

    private static final String TOPIC = "bankflow.transfer.completed.v1";

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public OutboxPublisher(
            OutboxEventRepository outboxEventRepository,
            KafkaTemplate<String, String> kafkaTemplate
    ) {
        this.outboxEventRepository = outboxEventRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelay = 1000)
    public void publishPendingEvents() {

        List<OutboxEvent> events =
                outboxEventRepository
                        .findTop100ByStatusOrderByCreatedAtAsc(
                                OutboxStatus.PENDING
                        );

        for (OutboxEvent event : events) {
            publish(event);
        }
    }

    private void publish(OutboxEvent event) {

        try {
            kafkaTemplate.send(
                    TOPIC,
                    event.getAggregateId().toString(),
                    event.getPayload()
            ).get();

            event.markPublished();
            outboxEventRepository.save(event);

            log.info(
                    "Outbox event published eventId={} aggregateId={}",
                    event.getId(),
                    event.getAggregateId()
            );

        } catch (Exception exception) {
            log.error(
                    "Unable to publish outbox event eventId={}",
                    event.getId(),
                    exception
            );
        }
    }
}