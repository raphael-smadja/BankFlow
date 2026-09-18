# BankFlow

BankFlow is a learning project exploring how to design a banking
system around transactional consistency and event-driven architecture.

## Why BankFlow?

A bank transfer looks simple:

1. Debit account A
2. Credit account B
3. Send a notification

But things become more interesting when failures happen.

What if the application crashes between the database transaction
and the Kafka publication?

BankFlow explores these problems through patterns such as
transactional boundaries, ledger entries and the Transactional Outbox Pattern.

## Architecture

account-service
├── Accounts
├── Transfers
├── Ledger
└── Outbox
│
▼
Kafka
│
▼
notification-service

## Services

### Account Service

Responsible for:
- account management
- transfers
- balance consistency
- ledger entries
- transactional outbox

### Notification Service

Consumes domain events from Kafka and handles notifications.

## Tech Stack

- Java
- Spring Boot
- PostgreSQL
- Kafka
- Flyway
- Docker / Docker Compose
- JUnit 5
- Mockito

## Concepts explored

- Transactional consistency
- Event-driven architecture
- Transactional Outbox Pattern
- Idempotency
- Database transactions
- Immutable ledger
- Asynchronous communication
- Failure handling

## Running locally

TODO

## Tests

TODO

## Roadmap

TODO