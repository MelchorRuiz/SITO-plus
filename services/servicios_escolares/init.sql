-- Modified SQL Script

CREATE DATABASE IF NOT EXISTS school_services_db;
USE school_services_db;

CREATE TABLE carrera
(
    id_carrera INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nombre_carrera VARCHAR(110) NOT NULL,
    estatus INT NOT NULL
);

CREATE TABLE grupo
(
    id_grupo INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nombre_grupo VARCHAR(10) NOT NULL,
    estatus INT NOT NULL,
    carrera_id INT NOT NULL,
    FOREIGN KEY (carrera_id) REFERENCES carrera(id_carrera)
);

CREATE TABLE alumno
(
    matricula INT NOT NULL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    nombre_usuario INT NOT NULL,
    contrasenia VARCHAR(20) NOT NULL,
    estatus INT NOT NULL,
    grupo_id INT NULL, 
    carrera_id INT NOT NULL, 
    FOREIGN KEY (carrera_id) REFERENCES carrera(id_carrera),
    FOREIGN KEY (grupo_id) REFERENCES grupo(id_grupo)
);

-- Inserts remain the same
INSERT INTO carrera(nombre_carrera, estatus) VALUES ("INGENIERIA EN TECNOLOGIAS DE LA INFORMACION E INNOVACION CON TSU EN ENTORNOS VIRTUALES Y NEGOCIOS DIGITALES", 1);
INSERT INTO carrera(nombre_carrera, estatus) VALUES ("INGENIERIA EN TECNOLOGIAS DE LA INFORMACION E INNOVACION CON TSU EN INFRAESTRUCTURA DE REDES DIGITALES", 1);
INSERT INTO carrera(nombre_carrera, estatus) VALUES ("INGENIERIA EN TECNOLOGIAS DE LA INFORMACION E INNOVACION CON TSU EN DESARROLLO DE SOFTWARE MULTIPLATAFORMA", 1);
INSERT INTO carrera(nombre_carrera, estatus) VALUES ("INGENIERIA EN TECNOLOGIAS DE LA INFORMACION E INNOVACION CON TSU INTELIGENCIA ARTIFICIAL", 1);
INSERT INTO carrera(nombre_carrera, estatus) VALUES ("INGENIERIA EN TECNOLOGIAS DE LA INFORMACION E INNOVACION CON TSU EN CIENCIA DE DATOS", 1);
INSERT INTO carrera(nombre_carrera, estatus) VALUES ("INGENIERIA EN MECATRONICA CON TSU EN SISTEMAS DE MANUFACTURA FLEXIBLE", 1);
INSERT INTO carrera(nombre_carrera, estatus) VALUES ("INGENIERIA EN MECATRONICA CON TSU EN OPTOMECATRONICA", 1);
INSERT INTO carrera(nombre_carrera, estatus) VALUES ("INGENIERIA EN MECATRONICA CON TSU EN AUTOMATIZACION", 1);
INSERT INTO carrera(nombre_carrera, estatus) VALUES ("INGENIERIA INDUSTRIAL CON TSU SISTEMAS PRODUCTIVOS", 1);
INSERT INTO carrera(nombre_carrera, estatus) VALUES ("INGENIERIA INDUSTRIAL CON TSU EN MOLDEO DE PLASTICOS", 1);
INSERT INTO carrera(nombre_carrera, estatus) VALUES ("INGENIERIA INDUSTRIAL CON TSU EN GESTION Y PRODUCTIVIDAD DE CALZADO", 1);
INSERT INTO carrera(nombre_carrera, estatus) VALUES ("INGENIERIA INDUSTRIAL CON TSU EN AUTOMOTRIZ", 1);
INSERT INTO grupo(nombre_grupo, estatus, carrera_id) VALUES ("IDGS703", 1, 3);
INSERT INTO grupo(nombre_grupo, estatus, carrera_id) VALUES ("EVN701", 1, 1);
-- INSERT INTO alumno(matricula, nombre, nombre_usuario, contrasenia, estatus, grupo_id, carrera_id) VALUES (23001523, "Sandro Alexis Ramirez Rios", 23001523, "23001523", 1, 2, 3);
-- INSERT INTO alumno(matricula, nombre, nombre_usuario, contrasenia,  estatus, carrera_id) VALUES (23003901, "Jaqueline Garcia", 23003901, "23003901", 1, 3);

-- Procedures (modified to include grupo_id for alumno, and add delete procedures)

DELIMITER $$
CREATE PROCEDURE agregarGrupo(
IN var_nombreGrupo 			VARCHAR(50),
IN var_carreraId			INT,
OUT var_grupoId				INT	
)
BEGIN
	START TRANSACTION; 
		INSERT INTO grupo(nombre_grupo, estatus, carrera_id) VALUES (var_nombreGrupo, 1, var_carreraId);
		SET var_grupoId = LAST_INSERT_ID();
    COMMIT;
END
$$
DELIMITER ; 

DELIMITER $$
CREATE PROCEDURE actualizarGrupo( 
IN var_nombreGrupo 			VARCHAR(50),
IN var_carreraId			INT,
IN var_grupoId				INT	
)
BEGIN
	UPDATE grupo SET nombre_grupo = var_nombreGrupo, carrera_id = var_carreraId WHERE id_grupo = var_grupoId;
END
$$
DELIMITER ; 

DELIMITER $$
CREATE PROCEDURE eliminarGrupo( 
IN var_grupoId				INT	
)
BEGIN
	UPDATE grupo SET estatus = 0 WHERE id_grupo = var_grupoId;
END
$$
DELIMITER ; 

DELIMITER $$
CREATE PROCEDURE agregarCarrera( 
IN var_nombreCarrera 		VARCHAR(110),
OUT var_idCarrera			INT	
)
BEGIN
	START TRANSACTION; 
		INSERT INTO carrera(nombre_carrera, estatus) VALUES (var_nombreCarrera, 1);
		SET var_idCarrera = LAST_INSERT_ID();
    COMMIT;
END
$$
DELIMITER ; 

DELIMITER $$
CREATE PROCEDURE actualizarCarrera( 
IN var_nombreCarrera			VARCHAR(110),
IN var_idCarrera				INT	
)
BEGIN
	UPDATE carrera SET nombre_carrera = var_nombreCarrera WHERE id_carrera = var_idCarrera;
END
$$
DELIMITER ; 

DELIMITER $$
CREATE PROCEDURE agregarAlumno( 
IN var_matricula			INT,
IN var_nombre	 			VARCHAR(50),
IN var_nombreUsuario		INT,
IN var_contrasenia 			VARCHAR(20),
IN var_carreraId			INT,
IN var_grupoId				INT
)
BEGIN
	START TRANSACTION; 
		INSERT INTO alumno(matricula, nombre, nombre_usuario, contrasenia, estatus, grupo_id, carrera_id) VALUES 
        (var_matricula, var_nombre, var_nombreUsuario, var_contrasenia, 1, var_grupoId, var_carreraId);
    COMMIT;
END
$$
DELIMITER ; 

DELIMITER $$
CREATE PROCEDURE actualizarAlumno( 
IN var_matricula			INT,
IN var_nombre	 			VARCHAR(50),
IN var_nombreUsuario		INT,
IN var_contrasenia 			VARCHAR(20),
IN var_carreraId			INT,
IN var_grupoId				INT
)
BEGIN
	START TRANSACTION; 
		UPDATE alumno SET nombre = var_nombre, nombre_usuario = var_nombreUsuario, contrasenia = var_contrasenia, carrera_id = var_carreraId, grupo_id = var_grupoId
        WHERE matricula = var_matricula;
    COMMIT;
END
$$
DELIMITER ; 

DELIMITER $$
CREATE PROCEDURE eliminarAlumno( 
IN var_matricula			INT
)
BEGIN
	UPDATE alumno SET estatus = 0 WHERE matricula = var_matricula;
END
$$
DELIMITER ; 

-- DELIMITER $$
-- CREATE PROCEDURE asignarAlumnoGrupo( 
-- IN var_matricula			INT,
-- IN var_grupoId				INT
-- )
-- BEGIN
-- 	UPDATE alumno SET grupo_id = var_grupoId WHERE matricula = var_matricula;
-- END
-- $$
-- DELIMITER ; 

-- DELIMITER $$
-- CREATE PROCEDURE removerAlumnoGrupo( 
-- IN var_matricula			INT
-- )
-- BEGIN
-- 	UPDATE alumno SET grupo_id = NULL WHERE matricula = var_matricula;
-- END
-- $$
-- DELIMITER ;
