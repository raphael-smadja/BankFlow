CREATE TABLE accounts
(
    id              UUID           NOT NULL,
    customer_id     UUID           NOT NULL,
    iban            VARCHAR(34)    NOT NULL,
    currency        VARCHAR(3)     NOT NULL,
    balance         NUMERIC(19, 2) NOT NULL,
    status          VARCHAR(30)    NOT NULL,
    version         BIGINT         NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ    NOT NULL,
    updated_at      TIMESTAMPTZ    NOT NULL,

    CONSTRAINT pk_accounts PRIMARY KEY (id),
    CONSTRAINT uk_accounts_iban UNIQUE (iban),
    CONSTRAINT ck_accounts_balance_positive CHECK (balance >= 0)
);

CREATE INDEX idx_accounts_customer_id
    ON accounts(customer_id);