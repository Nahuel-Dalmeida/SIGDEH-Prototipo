# SIGDEH – Sistema de Gestión y Priorización de la Demanda Habitacional

Prototipo en Java del sistema para el Instituto Municipal de Vivienda y Hábitat (IMVH) de San Martín de los Andes. Registra las solicitudes de los grupos familiares, valida los requisitos normativos y emite listados de prioridad por operatoria. Desde el TP4 los datos se guardan en MySQL.

## Alcance del prototipo
- Registrar, actualizar y consultar solicitudes (CU001, CU002, CU003).
- Emitir el listado de prioridad por universo, con cálculo de puntaje (CU006, CU007).
- Guardar los listados en la base, consultar su historial y exportarlos a CSV.
- Dar de baja solicitudes y eliminar las dadas de baja.

## Tecnologías
- Java 17 y Maven
- MySQL 8 con JDBC (mysql-connector-j)
- IntelliJ IDEA
- Menú de consola

## Estructura de paquetes (`com.imvh.sigdeh`)
| Paquete | Contenido |
| --- | --- |
| `excepciones` | Excepciones propias del sistema, incluida la de persistencia |
| `util` | Parámetros normativos, ordenación y exportación a CSV |
| `modelo` | Entidades del dominio |
| `priorizacion` | Motor de puntaje (patrón Strategy) |
| `dao` | Interfaces de acceso a datos |
| `dao.mysql` | Conexión `ConexionBD` (Singleton), clase base abstracta e implementaciones JDBC |
| `gestor` | Reglas de negocio |
| `vista` | Menú de consola |

## Cómo ejecutar
1. Crear la base ejecutando, en orden, `sql/01_esquema.sql` y `sql/02_datos.sql` (MySQL Workbench o consola).
2. Renombrar `config/db.properties.ejemplo` como `config/db.properties.ejemplo` y completar usuario y clave de MySQL.
3. Abrir el proyecto en IntelliJ IDEA con un JDK 17 o superior y dejar que Maven descargue el driver.
4. Ejecutar la clase `com.imvh.sigdeh.Main`.
5. Si los acentos se ven mal en la consola, agregar `-Dfile.encoding=UTF-8` a las opciones de la VM.

## Menú
```
 1. Registrar solicitud
 2. Actualizar solicitud
 3. Consultar estado de una solicitud
 4. Buscar solicitud por DNI
 5. Listar solicitudes registradas
 6. Emitir listado de prioridad
 7. Ver historial de listados emitidos
 8. Dar de baja una solicitud
 9. Eliminar una solicitud dada de baja
 0. Salir
```

## Datos iniciales
`02_datos.sql` carga cuatro grupos familiares, una operatoria abierta (con un universo general y uno de discapacidad) y el esquema de priorización versión 3 con tres criterios: antigüedad de residencia, ingreso per cápita y vulnerabilidad. Se puede probar directamente la opción 6.

## Autor
Nahuel Ezequiel d'Almeida – Seminario de Práctica de Informática, Universidad Siglo 21.
