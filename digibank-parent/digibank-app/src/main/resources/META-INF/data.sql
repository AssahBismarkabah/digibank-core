-- Seed data for local development
INSERT INTO customers (id, first_name, last_name, email) VALUES (1, 'John', 'Doe', 'john.doe@example.com') ON CONFLICT DO NOTHING;
INSERT INTO accounts (id, account_number, balance, customer_id, account_type, currency) VALUES (1, 'DE1234567890', 1000.00, 1, 'CHECKING', 'EUR') ON CONFLICT DO NOTHING;
INSERT INTO accounts (id, account_number, balance, customer_id, account_type, currency) VALUES (2, 'DE0987654321', 5000.00, 1, 'SAVINGS', 'EUR') ON CONFLICT DO NOTHING;
