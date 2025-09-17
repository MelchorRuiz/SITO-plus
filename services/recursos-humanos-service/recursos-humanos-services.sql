DROP DATABASE IF EXISTS recursos_humanos;
-- Crear base de datos (si no existe)
CREATE DATABASE IF NOT EXISTS recursos_humanos;

-- Usar la base de datos
USE recursos_humanos;

-- Crear la tabla Profesor
CREATE TABLE profesores (
    id BIGINT NOT NULL AUTO_INCREMENT,
    numero_empleado VARCHAR(50) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    materia VARCHAR(100),
    salario DECIMAL(10,2),
    hashed_password VARCHAR(255) NOT NULL,
    puesto VARCHAR(50) NOT NULL,
    PRIMARY KEY (id)
);

