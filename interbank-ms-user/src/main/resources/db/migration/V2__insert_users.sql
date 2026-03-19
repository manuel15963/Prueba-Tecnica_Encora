USE WalletDB;

-- truncate table `WalletDB`.`user`;
-- DELETE FROM `WalletDB`.`user`;
INSERT into `WalletDB`.`user`(document, type_document, firstname, lastname, address, email, phone_number, enabled, created_date)
VALUES
    ('12345678', 'DNI', 'Juan', 'Perez', 'Av. Lima 123', 'juan.perez@email.com', '999888777', TRUE, NOW()),
    ('A1234567', 'PASSPORT', 'Maria', 'Lopez', 'Calle Sol 456', 'maria.lopez@email.com', '988777666', TRUE, NOW()),
    ('87654321', 'DNI', 'Admin', 'Master', 'Av. Central 789', 'admin@email.com', '977666555', TRUE, NOW());

-- Tabla de Cuentas
INSERT into `WalletDB`.`account`(phone_number, document, account_number, banking_entity, device_serial, daily_limit, operation_limit, username, password, role, currency, created_date)
VALUES
    ('999888777', '12345678', '123456789012', 'Banco Nacional', 'DEVICE123', 2000.00, 10000.00, 'juanperez', '$2a$10$4G7dZGVX0q0HRBg6C5q0eOVUKcbKTQnxTBBSMLlQA8/7j3n4J7LQ.', 'USER', 'PEN', NOW()),
    ('988777666', 'A1234567', '987654321098', 'Banco Global', 'DEVICE456', 1500.00, 7000.00, 'marialopez', '$2a$10$4G7dZGVX0q0HRBg6C5q0eOVUKcbKTQnxTBBSMLlQA8/7j3n4J7LQ.', 'USER', 'USD', NOW()),
    ('977666555', '87654321', '112233445566', 'Banco Internacional', 'DEVICE789', 3000.00, 15000.00, 'adminmaster', '$2a$10$4G7dZGVX0q0HRBg6C5q0eOVUKcbKTQnxTBBSMLlQA8/7j3n4J7LQ.', 'ADMIN', 'EUR', NOW());

-- Tabla de Transferencias
INSERT into `WalletDB`.`transfer`(origin_number, origin_account, target_number, target_account, amount, transfer_type, transfer_status, created_date)
VALUES
    ('999888777', '123456789012', '988777666', '987654321098', 150.00, 'TRANSFER', 'COMPLETED', NOW()),
    ('988777666', '987654321098', '977666555', '112233445566', 200.00, 'DEPOSIT', 'COMPLETED', NOW()),
    ('977666555', '112233445566', '999888777', '123456789012', 50.00, 'WITHDRAWAL', 'PENDING', NOW());

-- Tabla de Saldos
INSERT into `WalletDB`.`balance`(phone_number, origin_account, balance_amount, last_update)
VALUES
    ('999888777', '123456789012', 1000.00, NOW()),
    ('988777666', '987654321098', 500.00, NOW()),
    ('977666555', '112233445566', 750.00, NOW());

-- Actualización de saldo después de una transferencia
UPDATE `WalletDB`.`balance`
SET balance_amount = balance_amount - 150.00
WHERE phone_number = '999888777';

UPDATE `WalletDB`.`balance`
SET balance_amount = balance_amount + 150.00
WHERE phone_number = '988777666';