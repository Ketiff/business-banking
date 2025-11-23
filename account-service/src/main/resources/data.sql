USE account_db;

INSERT INTO accounts (account_number, account_type, initial_balance, current_balance, status, customer_id) VALUES
('478758', 'SAVINGS', 2000.00, 1425.00, TRUE, 1),
('225487', 'CHECKING', 100.00, 700.00, TRUE, 2),
('495878', 'SAVINGS', 0.00, 150.00, TRUE, 3),
('496825', 'SAVINGS', 540.00, 0.00, TRUE, 2),
('585545', 'CHECKING', 1000.00, 1000.00, TRUE, 1)
ON DUPLICATE KEY UPDATE id=id;

INSERT INTO movements (date, movement_type, amount, balance, account_id) VALUES
('2022-02-08 10:30:00', 'DEBIT', 575.00, 1425.00, 1),
('2022-02-10 14:20:00', 'CREDIT', 600.00, 700.00, 2),
('2022-02-09 09:15:00', 'CREDIT', 150.00, 150.00, 3),
('2022-02-08 15:45:00', 'DEBIT', 540.00, 0.00, 4)
ON DUPLICATE KEY UPDATE id=id;