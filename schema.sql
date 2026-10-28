-- Script de creación de base de datos y tabla para denunciaservice

CREATE DATABASE IF NOT EXISTS denunciasdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE denunciasdb;

CREATE TABLE IF NOT EXISTS denuncias (
    id INT AUTO_INCREMENT PRIMARY KEY,
    dni VARCHAR(8) NOT NULL,
    fecha DATETIME NOT NULL,
    tipo VARCHAR(30) NOT NULL,
    direccion VARCHAR(200) NOT NULL,
    descripcion VARCHAR(255) NOT NULL,
    estado INT NOT NULL DEFAULT 1
);
