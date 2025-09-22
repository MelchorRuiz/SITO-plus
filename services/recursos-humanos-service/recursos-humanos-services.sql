DROP DATABASE IF EXISTS human_resources_db;
-- Crear base de datos (si no existe)
CREATE DATABASE IF NOT EXISTS human_resources_db;

-- Usar la base de datos
USE human_resources_db;

-- Crear la tabla Profesor
CREATE TABLE profesores (
    id BIGINT NOT NULL AUTO_INCREMENT,
    numero_empleado VARCHAR(50) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    salario DECIMAL(10,2),
    puesto VARCHAR(50) NOT NULL,
    PRIMARY KEY (id)
);

