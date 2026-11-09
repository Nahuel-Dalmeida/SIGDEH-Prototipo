-- =====================================================================
-- SIGDEH - Datos iniciales (TP4)
-- Familias Gómez y Pérez del TP2, más Ramírez y Sosa agregadas en el TP3.
-- No se carga el listado de ejemplo del TP2: el sistema lo emite con la opción 6.
-- =====================================================================
USE sigdeh;

INSERT INTO grupos_familiares (situacion_habitacional, ingreso_neto, fecha_inicio_residencia) VALUES
('Alquiler', 850000.00, '2019-03-15'),            -- 1 Gómez
('Vivienda precaria', 620000.00, '2015-07-01'),   -- 2 Pérez (titular con discapacidad)
('Alquiler', 400000.00, '2023-01-10'),            -- 3 Ramírez (menos de 5 años de residencia)
('Vivienda precaria', 300000.00, '2017-09-20');   -- 4 Sosa (familia numerosa)

INSERT INTO integrantes (grupo_id, dni, apellido_nombre, fecha_nacimiento, vinculo, discapacidad) VALUES
(1, '30123456', 'Gomez, Marta', '1985-04-12', 'Titular', FALSE),
(1, '45123456', 'Gomez, Lucas', '2010-09-01', 'Hijo/a', FALSE),
(2, '28456789', 'Perez, Raul', '1978-11-23', 'Titular', TRUE),
(3, '33222111', 'Ramirez, Julia', '1990-02-02', 'Titular', FALSE),
(4, '27111222', 'Sosa, Carlos', '1983-06-30', 'Titular', FALSE),
(4, '50111222', 'Sosa, Ana', '2015-01-10', 'Hijo/a', FALSE),
(4, '51111222', 'Sosa, Pedro', '2018-03-05', 'Hijo/a', FALSE);

INSERT INTO solicitudes (grupo_id, fecha_alta, fecha_ultima_actualizacion, estado) VALUES
(1, '2024-05-10', '2026-08-20', 'ACTIVA'),
(2, '2020-02-18', '2026-06-05', 'ACTIVA'),
(3, '2024-03-01', '2025-03-01', 'ACTIVA'),
(4, '2023-08-14', '2026-09-01', 'ACTIVA');

INSERT INTO operatorias (tipo_solucion, cantidad_disponible, fecha_convocatoria, estado) VALUES
('Lote con servicios', 40, '2026-10-01', 'ABIERTA');

INSERT INTO universos (operatoria_id, nombre, cupo, requiere_discapacidad) VALUES
(1, 'Universo General', 30, FALSE),
(1, 'Universo Discapacidad', 10, TRUE);

INSERT INTO esquemas_priorizacion (version, fecha_vigencia) VALUES
(3, '2026-01-01');

INSERT INTO criterios (esquema_id, nombre, peso, estrategia) VALUES
(1, 'Antiguedad de residencia', 0.30, 'ANTIGUEDAD_RESIDENCIA'),
(1, 'Ingreso per capita', 0.40, 'INGRESO_PER_CAPITA'),
(1, 'Vulnerabilidad del grupo familiar', 0.30, 'VULNERABILIDAD');
