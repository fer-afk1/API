-- Health&Blood: esquema de base de datos (solo tablas)
-- Ejecutar conectado a la base destino (p. ej. defaultdb en Aiven Cloud)

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
    tipo_lectura VARCHAR(45),
    notas TEXT NULL,
    fecha_registro DATE,
    FOREIGN KEY(id_usuario) REFERENCES users(id_usuario)
);
