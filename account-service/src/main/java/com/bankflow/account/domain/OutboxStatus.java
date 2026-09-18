package com.bankflow.account.domain;

public enum OutboxStatus {
    PENDING,
    PUBLISHED,
    FAILED
}