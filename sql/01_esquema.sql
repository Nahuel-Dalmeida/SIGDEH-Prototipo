-- =====================================================================
-- SIGDEH - Creación de la base de datos (TP4)
-- MySQL 8.0, motor InnoDB, codificación utf8mb4.
-- Parte de las tablas definidas en el TP2. Cambios respecto del TP2:
--   * universos.requiere_discapacidad: indica qué universo es el de discapacidad.
--   * criterios.estrategia: indica qué clase Java calcula el puntaje del criterio.
--   * estados guardados en mayúsculas (ACTIVA, ABIERTA...), igual que los enum de Java.
--   * restricciones CHECK sobre montos, cupos y pesos.
-- Ejecutar primero este script y después 02_datos.sql
-- =====================================================================

DROP DATABASE IF EXISTS sigdeh;
CREATE DATABASE sigdeh CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci;
USE sigdeh;

CREATE TABLE grupos_familiares (
    grupo_id INT AUTO_INCREMENT PRIMARY KEY,
    situacion_habitacional VARCHAR(100) NOT NULL,
    ingreso_neto DECIMAL(10,2) NOT NULL CHECK (ingreso_neto >= 0),
    fecha_inicio_residencia DATE NOT NULL
) ENGINE = InnoDB;

CREATE TABLE integrantes (
    integrante_id INT AUTO_INCREMENT PRIMARY KEY,
    grupo_id INT NOT NULL,
    dni VARCHAR(15) NOT NULL UNIQUE,
    apellido_nombre VARCHAR(150) NOT NULL,
    fecha_nacimiento DATE NOT NULL,
    vinculo VARCHAR(50) NOT NULL,
    discapacidad BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (grupo_id) REFERENCES grupos_familiares(grupo_id)
) ENGINE = InnoDB;

CREATE TABLE solicitudes (
    solicitud_id INT AUTO_INCREMENT PRIMARY KEY,
    grupo_id INT NOT NULL UNIQUE,
    fecha_alta DATE NOT NULL,
    fecha_ultima_actualizacion DATE NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVA',
    FOREIGN KEY (grupo_id) REFERENCES grupos_familiares(grupo_id)
) ENGINE = InnoDB;

CREATE TABLE operatorias (
    operatoria_id INT AUTO_INCREMENT PRIMARY KEY,
    tipo_solucion VARCHAR(100) NOT NULL,
    cantidad_disponible INT NOT NULL CHECK (cantidad_disponible > 0),
    fecha_convocatoria DATE NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'ABIERTA'
) ENGINE = InnoDB;

CREATE TABLE universos (
    universo_id INT AUTO_INCREMENT PRIMARY KEY,
    operatoria_id INT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    cupo INT NOT NULL CHECK (cupo >= 0),
    requiere_discapacidad BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (operatoria_id) REFERENCES operatorias(operatoria_id)
) ENGINE = InnoDB;

CREATE TABLE esquemas_priorizacion (
    esquema_id INT AUTO_INCREMENT PRIMARY KEY,
    version INT NOT NULL UNIQUE,
    fecha_vigencia DATE NOT NULL
) ENGINE = InnoDB;

CREATE TABLE criterios (
    criterio_id INT AUTO_INCREMENT PRIMARY KEY,
    esquema_id INT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    peso DECIMAL(4,2) NOT NULL CHECK (peso > 0 AND peso <= 1),
    estrategia VARCHAR(30) NOT NULL,
    FOREIGN KEY (esquema_id) REFERENCES esquemas_priorizacion(esquema_id)
) ENGINE = InnoDB;

CREATE TABLE listados_prioridad (
    listado_id INT AUTO_INCREMENT PRIMARY KEY,
    operatoria_id INT NOT NULL,
    esquema_id INT NOT NULL,
    fecha_emision DATETIME NOT NULL,
    FOREIGN KEY (operatoria_id) REFERENCES operatorias(operatoria_id),
    FOREIGN KEY (esquema_id) REFERENCES esquemas_priorizacion(esquema_id)
) ENGINE = InnoDB;

CREATE TABLE posiciones (
    posicion_id INT AUTO_INCREMENT PRIMARY KEY,
    listado_id INT NOT NULL,
    solicitud_id INT NOT NULL,
    universo_id INT NOT NULL,
    puntaje DECIMAL(6,2) NOT NULL,
    orden INT NOT NULL,
    FOREIGN KEY (listado_id) REFERENCES listados_prioridad(listado_id),
    FOREIGN KEY (solicitud_id) REFERENCES solicitudes(solicitud_id),
    FOREIGN KEY (universo_id) REFERENCES universos(universo_id)
) ENGINE = InnoDB;

-- Seguridad (CU009 y CU010). Se crean para respetar el DER del TP2,
-- pero el prototipo todavía no implementa el inicio de sesión ni la bitácora.
CREATE TABLE usuarios (
    usuario_id INT AUTO_INCREMENT PRIMARY KEY,
    nombre_usuario VARCHAR(50) NOT NULL UNIQUE,
    hash_password VARCHAR(255) NOT NULL,
    perfil VARCHAR(20) NOT NULL
) ENGINE = InnoDB;

CREATE TABLE bitacora (
    bitacora_id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT NOT NULL,
    fecha_hora DATETIME NOT NULL,
    operacion VARCHAR(50) NOT NULL,
    detalle VARCHAR(255),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(usuario_id)
) ENGINE = InnoDB;
