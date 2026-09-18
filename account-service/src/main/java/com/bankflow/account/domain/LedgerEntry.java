package com.bankflow.account.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "ledger_entries")
public class LedgerEntry {

    @Id
    private UUID id;

    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    @Column(name = "transaction_id", nullable = false)
    private UUID transactionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EntryType type;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected LedgerEntry() {
        // JPA
    }

    private LedgerEntry(
            UUID id,
            UUID accountId,
            UUID transactionId,
            EntryType type,
            BigDecimal amount,
            String currency,
            Instant createdAt
    ) {
        this.id = id;
        this.accountId = accountId;
        this.transactionId = transactionId;
        this.type = type;
        this.amount = amount;
        this.currency = currency;
        this.createdAt = createdAt;
    }

    public static LedgerEntry debit(
            UUID accountId,
            UUID transactionId,
            BigDecimal amount,
            String currency
    ) {
        return new LedgerEntry(
                UUID.randomUUID(),
                accountId,
                transactionId,
                EntryType.DEBIT,
                amount,
                currency,
                Instant.now()
        );
    }

    public static LedgerEntry credit(
            UUID accountId,
            UUID transactionId,
            BigDecimal amount,
            String currency
    ) {
        return new LedgerEntry(
                UUID.randomUUID(),
                accountId,
                transactionId,
                EntryType.CREDIT,
                amount,
                currency,
                Instant.now()
        );
    }

    public UUID getId() {
        return id;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public EntryType getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}