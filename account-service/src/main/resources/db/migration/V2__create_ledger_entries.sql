CREATE TABLE ledger_entries
(
    id             UUID           NOT NULL,
    account_id     UUID           NOT NULL,
    transaction_id UUID           NOT NULL,
    type           VARCHAR(20)    NOT NULL,
    amount         NUMERIC(19, 2) NOT NULL,
    currency       VARCHAR(3)     NOT NULL,
    created_at     TIMESTAMPTZ    NOT NULL,

    CONSTRAINT pk_ledger_entries PRIMARY KEY (id),
    CONSTRAINT fk_ledger_account
        FOREIGN KEY (account_id)
        REFERENCES accounts(id),

    CONSTRAINT ck_ledger_amount_positive
        CHECK (amount > 0),

    CONSTRAINT ck_ledger_type
        CHECK (type IN ('DEBIT', 'CREDIT'))
);

CREATE INDEX idx_ledger_account_id
    ON ledger_entries(account_id);

CREATE INDEX idx_ledger_transaction_id
    ON ledger_entries(transaction_id);