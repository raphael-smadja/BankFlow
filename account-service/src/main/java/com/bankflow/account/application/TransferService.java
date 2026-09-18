package com.bankflow.account.application;

import com.bankflow.account.api.TransferCompletedEvent;
import com.bankflow.account.domain.Account;
import com.bankflow.account.domain.AccountNotFoundException;
import com.bankflow.account.domain.LedgerEntry;
import com.bankflow.account.domain.OutboxEvent;
import com.bankflow.account.infrastructure.AccountRepository;
import com.bankflow.account.infrastructure.LedgerEntryRepository;
import com.bankflow.account.infrastructure.OutboxEventRepository;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.core.JacksonException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
public class TransferService {

    private static final Logger log = LoggerFactory.getLogger(TransferService.class);

    private final AccountRepository accountRepository;
    private final LedgerEntryRepository ledgerEntryRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    public TransferService(
            AccountRepository accountRepository,
            LedgerEntryRepository ledgerEntryRepository,
            OutboxEventRepository outboxEventRepository,
            ObjectMapper objectMapper
    ) {
        this.accountRepository = accountRepository;
        this.ledgerEntryRepository = ledgerEntryRepository;
        this.outboxEventRepository = outboxEventRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public UUID transfer(
            UUID sourceAccountId,
            UUID destinationAccountId,
            BigDecimal amount
    ) {

        if (sourceAccountId.equals(destinationAccountId)) {
            throw new IllegalArgumentException(
                    "Source and destination accounts must differ"
            );
        }

        Account source = accountRepository.findById(sourceAccountId).orElseThrow(() ->
                        new AccountNotFoundException(sourceAccountId)
        );

        Account destination = accountRepository
                .findById(destinationAccountId)
                .orElseThrow(() ->
                        new AccountNotFoundException(destinationAccountId)
                );

        if (!source.getCurrency()
                .equals(destination.getCurrency())) {
            throw new IllegalArgumentException(
                    "Cross-currency transfer is not supported"
            );
        }

        source.withdraw(amount);
        destination.deposit(amount);

        UUID transactionId = UUID.randomUUID();

        LedgerEntry debit = LedgerEntry.debit(
                source.getId(),
                transactionId,
                amount,
                source.getCurrency()
        );

        LedgerEntry credit = LedgerEntry.credit(
                destination.getId(),
                transactionId,
                amount,
                destination.getCurrency()
        );

        ledgerEntryRepository.save(debit);
        ledgerEntryRepository.save(credit);

        TransferCompletedEvent event =
                new TransferCompletedEvent(
                        UUID.randomUUID(),
                        transactionId,
                        sourceAccountId,
                        destinationAccountId,
                        amount,
                        source.getCurrency(),
                        Instant.now()
                );

        String payload;

        try {
            payload = objectMapper.writeValueAsString(event);
        } catch (JacksonException exception) {
            throw new IllegalStateException(
                    "Unable to serialize TransferCompletedEvent",
                    exception
            );
        }

        OutboxEvent outboxEvent = OutboxEvent.pending(
                "TRANSFER",
                transactionId,
                "TransferCompletedEvent",
                payload
        );

        outboxEventRepository.save(outboxEvent);

        log.info(
                "Transfer completed transactionId={} sourceAccountId={} destinationAccountId={} amount={}",
                transactionId,
                sourceAccountId,
                destinationAccountId,
                amount
        );

        return transactionId;
    }
}