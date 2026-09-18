package com.bankflow.account.domain;

import java.math.BigDecimal;
import java.util.UUID;

public class InsufficientBalanceException extends RuntimeException {

    public InsufficientBalanceException(UUID accountId, BigDecimal balance, BigDecimal requestedAmount) {
        super("Insufficient balance for account %s. Balance=%s requested=%s".formatted(accountId, balance,
                requestedAmount));
    }
}