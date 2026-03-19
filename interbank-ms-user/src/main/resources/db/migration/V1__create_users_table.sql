DROP DATABASE WalletDB;
CREATE DATABASE IF NOT EXISTS WalletDB;
USE WalletDB;



-- Tabla de Usuarios

CREATE TABLE IF NOT EXISTS `WalletDB`.`user`(
                                                document VARCHAR(50) NOT NULL UNIQUE PRIMARY KEY,
    type_document ENUM('DNI', 'PASSPORT', 'OTHER') NOT NULL,
    firstname VARCHAR(255) NOT NULL,
    lastname VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20) NOT NULL UNIQUE,
    enabled BOOLEAN DEFAULT TRUE,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );

-- Tabla de Cuentas
CREATE TABLE IF NOT EXISTS `WalletDB`.`account`(
                                                   phone_number VARCHAR(20) NOT NULL UNIQUE PRIMARY KEY,
    document VARCHAR(50) NOT NULL,
    account_number VARCHAR(30) NOT NULL UNIQUE,
    banking_entity VARCHAR(100) NOT NULL,
    device_serial VARCHAR(255) NOT NULL,
    daily_limit DECIMAL(15,2) NOT NULL DEFAULT 1000.00,
    operation_limit DECIMAL(15,2) NOT NULL DEFAULT 5000.00,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'USER') DEFAULT 'USER',
    currency ENUM('PEN', 'USD', 'EUR') DEFAULT 'PEN',
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (document) REFERENCES `WalletDB`.`user`(document) ON DELETE CASCADE
    );

-- Tabla de Transacciones
CREATE TABLE IF NOT EXISTS `WalletDB`.`transfer`(
                                                    origin_number VARCHAR(20) NOT NULL PRIMARY KEY,
    origin_account VARCHAR(30) NOT NULL,
    target_number VARCHAR(20) NOT NULL,
    target_account VARCHAR(30) NOT NULL,
    amount DECIMAL(15,2) NOT NULL CHECK (amount > 0),
    transfer_type ENUM('DEPOSIT', 'WITHDRAWAL', 'TRANSFER') NOT NULL,
    transfer_status ENUM('PENDING', 'COMPLETED', 'FAILED') DEFAULT 'PENDING',
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (origin_number) REFERENCES `WalletDB`.`account`(phone_number) ON DELETE CASCADE,
    FOREIGN KEY (target_number) REFERENCES `WalletDB`.`account`(phone_number) ON DELETE CASCADE
    );

-- Tabla de Saldo (para optimización y auditoría)
CREATE TABLE IF NOT EXISTS `WalletDB`.`balance`(
                                                   phone_number VARCHAR(20)  NOT NULL PRIMARY KEY,
    origin_account VARCHAR(30) NOT NULL,
    balance_amount DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    last_update TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (phone_number) REFERENCES `WalletDB`.`account`(phone_number) ON DELETE CASCADE
    );