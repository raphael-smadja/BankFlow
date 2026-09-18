package com.bankflow.account.domain;

import java.util.UUID;

public class AccountNotFoundException
        extends RuntimeException {

    public AccountNotFoundException(UUID id) {
        super(
                "Account %s was not found".formatted(id)
        );
    }
}