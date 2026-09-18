package com.bankflow.account.domain;

public class IbanAlreadyExistsException
        extends RuntimeException {

    public IbanAlreadyExistsException(String iban) {
        super(
                "An account already exists with IBAN " + iban
        );
    }
}