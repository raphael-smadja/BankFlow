package com.bankflow.account.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.UUID;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    private UUID id;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(nullable = false, unique = true, length = 34)
    private String iban;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AccountStatus status;

    @Version
    private long version;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Account() {
        // JPA
    }

    private Account(
            UUID id,
            UUID customerId,
            String iban,
            String currency,
            BigDecimal balance,
            AccountStatus status,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.customerId = customerId;
        this.iban = iban;
        this.currency = currency;
        this.balance = balance;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Account create(
            UUID customerId,
            String iban,
            Currency currency,
            BigDecimal initialBalance
    ) {
        if (customerId == null) {
            throw new IllegalArgumentException(
                    "Customer id is required"
            );
        }

        if (iban == null || iban.isBlank()) {
            throw new IllegalArgumentException(
                    "IBAN is required"
            );
        }

        if (currency == null) {
            throw new IllegalArgumentException(
                    "Currency is required"
            );
        }

        if (initialBalance == null
                || initialBalance.signum() < 0) {
            throw new IllegalArgumentException(
                    "Initial balance cannot be negative"
            );
        }

        Instant now = Instant.now();

        return new Account(
                UUID.randomUUID(),
                customerId,
                iban,
                currency.getCurrencyCode(),
                initialBalance,
                AccountStatus.ACTIVE,
                now,
                now
        );
    }

    public UUID getId() {
        return id;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public String getIban() {
        return iban;
    }

    public String getCurrency() {
        return currency;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public long getVersion() {
        return version;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void deposit(BigDecimal amount) {
        ensureActive();
        ensurePositiveAmount(amount);

        this.balance = this.balance.add(amount);
        this.updatedAt = Instant.now();
    }

    public void withdraw(BigDecimal amount) {
        ensureActive();
        ensurePositiveAmount(amount);

        if (balance.compareTo(amount) < 0) {
            throw new InsufficientBalanceException(
                    id,
                    balance,
                    amount
            );
        }

        this.balance = this.balance.subtract(amount);
        this.updatedAt = Instant.now();
    }

    private void ensureActive() {
        if (status != AccountStatus.ACTIVE) {
            throw new AccountNotActiveException(id);
        }
    }

    private void ensurePositiveAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException(
                    "Amount must be strictly positive"
            );
        }
    }
}