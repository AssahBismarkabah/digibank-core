-- =====================================================================
-- Digi Bank - V2: Seed (demonstration) data
-- Reproducible seed dataset used by local dev, smoke tests and DAST.
-- Ids are explicitly set so that inter-table references stay stable
-- across environments and re-runs.
-- =====================================================================

INSERT INTO customers (id, first_name, last_name, email, created_at, updated_at)
VALUES (1, 'John', 'Doe', 'john.doe@example.com', NOW(), NOW());

INSERT INTO accounts (id, account_number, balance, customer_id, account_type, currency, created_at, updated_at)
VALUES (1, 'DE1234567890', 1000.00, 1, 'CHECKING', 'EUR', NOW(), NOW());

INSERT INTO accounts (id, account_number, balance, customer_id, account_type, currency, created_at, updated_at)
VALUES (2, 'DE0987654321', 5000.00, 1, 'SAVINGS', 'EUR', NOW(), NOW());

INSERT INTO transactions (id, account_id, amount, transaction_type, description, reference_number, transaction_date, created_at, updated_at)
VALUES (1, 1, 100.00, 'DEPOSIT', 'Initial deposit for demo account', '11111111-1111-1111-1111-111111111111', NOW(), NOW(), NOW());

INSERT INTO transactions (id, account_id, amount, transaction_type, description, reference_number, transaction_date, created_at, updated_at)
VALUES (2, 1, 25.00, 'WITHDRAWAL', 'ATM withdrawal for demo', '22222222-2222-2222-2222-222222222222', NOW(), NOW(), NOW());

INSERT INTO compliance_checks (id, customer_id, check_type, status, checked_by, remarks, check_date, created_at, updated_at)
VALUES (1, 1, 'AML_SCREENING', 'PASSED', 'system', 'Demo compliance check passed', NOW(), NOW(), NOW());

-- Ensure sequences are aligned with the explicit ids above so future
-- inserts via the application do not collide.
SELECT setval('customers_id_seq', (SELECT MAX(id) FROM customers));
SELECT setval('accounts_id_seq', (SELECT MAX(id) FROM accounts));
SELECT setval('transactions_id_seq', (SELECT MAX(id) FROM transactions));
SELECT setval('compliance_checks_id_seq', (SELECT MAX(id) FROM compliance_checks));
