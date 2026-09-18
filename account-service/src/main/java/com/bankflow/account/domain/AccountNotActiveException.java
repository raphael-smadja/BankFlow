package com.bankflow.account.domain;

import java.util.UUID;

public class AccountNotActiveException
        extends RuntimeException {

    public AccountNotActiveException(UUID accountId) {
        super(
                "Account %s is not active"
                        .formatted(accountId)
        );
    }
}