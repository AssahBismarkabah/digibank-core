-- =====================================================================
-- Digi Bank - V1: Initial schema
-- Versioned schema definition managed by Flyway.
-- Mirrors the JPA entity model so that Hibernate (ddl-auto: validate)
-- can confirm the schema matches the entities on startup.
-- =====================================================================

CREATE TABLE customers (
    id          BIGSERIAL PRIMARY KEY,
    first_name  VARCHAR(50)  NOT NULL,
    last_name   VARCHAR(50)  NOT NULL,
    email       VARCHAR(100) NOT NULL,
    created_at  TIMESTAMP    NOT NULL,
    updated_at  TIMESTAMP,
    CONSTRAINT uk_customers_email UNIQUE (email)
);

CREATE TABLE accounts (
    id             BIGSERIAL PRIMARY KEY,
    account_number VARCHAR(20)  NOT NULL,
    balance        NUMERIC(14,2) NOT NULL,
    customer_id    BIGINT       NOT NULL,
    account_type   VARCHAR(20)  NOT NULL,
    currency       VARCHAR(3),
    created_at     TIMESTAMP    NOT NULL,
    updated_at     TIMESTAMP,
    CONSTRAINT uk_accounts_number UNIQUE (account_number)
);

CREATE TABLE transactions (
    id               BIGSERIAL PRIMARY KEY,
    account_id       BIGINT       NOT NULL,
    amount           NUMERIC(14,2) NOT NULL,
    transaction_type VARCHAR(20)  NOT NULL,
    description      VARCHAR(500),
    reference_number VARCHAR(36)  NOT NULL,
    transaction_date TIMESTAMP    NOT NULL,
    created_at       TIMESTAMP    NOT NULL,
    updated_at       TIMESTAMP,
    CONSTRAINT uk_transactions_reference UNIQUE (reference_number)
);

CREATE TABLE compliance_checks (
    id          BIGSERIAL PRIMARY KEY,
    customer_id BIGINT      NOT NULL,
    check_type  VARCHAR(30) NOT NULL,
    status      VARCHAR(20) NOT NULL,
    checked_by  VARCHAR(50) NOT NULL,
    remarks     TEXT,
    check_date  TIMESTAMP   NOT NULL,
    created_at  TIMESTAMP   NOT NULL,
    updated_at  TIMESTAMP
);
