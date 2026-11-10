-- Base de datos para Healt&Blood
-- Fecha 15-10-26
-- v1

DROP DATABASE IF EXISTS healthBlood;

CREATE DATABASE healthBlood;

USE healthBlood;

CREATE TABLE users(
    id_usuario INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
 nombre VARCHAR(45) NOT NULL,
    primer_apellido VARCHAR(45) NOT NULL,
    segundo_apellido VARCHAR(45),
    fecha_nacimiento DATE,
    tipo_diabetes VARCHAR(30),
    fecha_registro DATE,
    email VARCHAR(50)
);

CREATE TABLE lectura_glucosa(
 id_lectura INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    valor_mg_dl SMALLINT UNSIGNED NOT NULL,
    tipo_lectura VARCHAR(45), -- AYUNAS, ANTES DE DORMIR, DESPUES DE ALIMENTOS
    notas TEXT NULL,
    fecha_registro DATE,
    FOREIGN KEY(id_usuario) REFERENCES users(id_usuario)
);
use healthBlood;
INSERT INTO users VALUES(
 0,
    "Cristian",
    "Estrada",
    "Andrade",
    "1997-11-23",
    "Tipo 1",
    NOW(),
    "cj@gmail.com"
);
INSERT INTO lectura_glucosa (id_usuario, valor_mg_dl, tipo_lectura, notas, fecha_registro)
VALUES (1, 98, 'AYUNAS', 'Se sintió bien', NOW());

SELECT * from users;








 