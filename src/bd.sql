DROP DATABASE IF EXISTS gluco_api;
CREATE DATABASE gluco_api;
USE gluco_api;
CREATE TABLE usuario(
	idUsuario INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    nombre varchar(40) NOT NULL,
    primerApellido VARCHAR(40) NOT NULL,
    segundoApellido VARCHAR(40),
    email VARCHAR(100) NOT NULL
);

CREATE TABLE medida(
	idMedida INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    idUsuario INT NOT NULL,
    glucosa INT NOT NULL,
    fechaHora DATETIME,
    contexto VARCHAR(100),
    notas LONGTEXT,
    FOREIGN KEY (idUsuario) REFERENCES usuario(idUsuario)
);