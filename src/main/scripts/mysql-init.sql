DROP DATABASE IF EXISTS phoneorderservice;
DROP USER IF EXISTS `phone_order_service`@`%`;
CREATE DATABASE IF NOT EXISTS phoneorderservice CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS `phone_order_service`@`%` IDENTIFIED WITH mysql_native_password BY 'password';
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, DROP, REFERENCES, INDEX, ALTER, EXECUTE, CREATE VIEW, SHOW VIEW,
CREATE ROUTINE, ALTER ROUTINE, EVENT, TRIGGER ON `phoneorderservice`.* TO `phone_order_service`@`%`;
FLUSH PRIVILEGES;