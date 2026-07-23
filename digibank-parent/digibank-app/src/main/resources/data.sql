-- Seed data for local development
-- Tables are created fresh by Hibernate (ddl-auto: create), so no conflict handling needed.
INSERT INTO customers (first_name, last_name, email, created_at, updated_at)
VALUES ('John', 'Doe', 'john.doe@example.com', NOW(), NOW());

INSERT INTO accounts (account_number, balance, customer_id, account_type, currency, created_at, updated_at)
VALUES ('DE1234567890', 1000.00, 1, 'CHECKING', 'EUR', NOW(), NOW());

INSERT INTO accounts (account_number, balance, customer_id, account_type, currency, created_at, updated_at)
VALUES ('DE0987654321', 5000.00, 1, 'SAVINGS', 'EUR', NOW(), NOW());
