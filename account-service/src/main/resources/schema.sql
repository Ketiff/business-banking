CREATE DATABASE IF NOT EXISTS account_db;
USE account_db;

CREATE TABLE IF NOT EXISTS accounts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_number VARCHAR(20) NOT NULL UNIQUE,
    account_type VARCHAR(20) NOT NULL,
    initial_balance DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    current_balance DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    status BOOLEAN NOT NULL DEFAULT TRUE,
    customer_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT chk_account_type CHECK (account_type IN ('SAVINGS', 'CHECKING'))
);

CREATE INDEX idx_account_number ON accounts(account_number);
CREATE INDEX idx_customer_id ON accounts(customer_id);

CREATE TABLE IF NOT EXISTS movements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    movement_type VARCHAR(20) NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    balance DECIMAL(15, 2) NOT NULL,
    account_id BIGINT NOT NULL,
    CONSTRAINT chk_movement_type CHECK (movement_type IN ('DEBIT', 'CREDIT')),
    CONSTRAINT chk_amount CHECK (amount > 0),
    CONSTRAINT fk_movements_account FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE
);

CREATE INDEX idx_movements_account_id ON movements(account_id);
CREATE INDEX idx_movements_date ON movements(date);