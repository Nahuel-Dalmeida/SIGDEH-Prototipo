# SIGDEH – Sistema de Gestión y Priorización de la Demanda Habitacional

Prototipo en Java (Trabajo Práctico 3) del sistema para el Instituto Municipal de Vivienda y Hábitat (IMVH) de San Martín de los Andes. Registra las solicitudes de los grupos familiares, valida los requisitos normativos y emite listados de prioridad por operatoria.

## Alcance del prototipo
- Registrar, actualizar y consultar solicitudes (CU001, CU002, CU003).
- Emitir el listado de prioridad por universo, con cálculo de puntaje (CU006, CU007).
- Los datos se guardan en memoria. La persistencia en MySQL se incorpora en el TP4.

## Tecnologías
- Java 17
- IntelliJ IDEA
- Menú de consola (sin dependencias externas)

## Estructura de paquetes (`com.imvh.sigdeh`)
| Paquete | Contenido |
| --- | --- |
| `excepciones` | Excepciones propias del sistema |
| `util` | Parámetros normativos y algoritmo de ordenación |
| `modelo` | Entidades del dominio |
| `priorizacion` | Motor de puntaje (patrón Strategy) |
| `dao` / `dao.memoria` | Interfaces de acceso a datos y su versión en memoria |
| `gestor` | Reglas de negocio |
| `vista` | Menú de consola |
| `datos` | Datos de ejemplo |

## Cómo ejecutar
1. Abrir el proyecto en IntelliJ IDEA con un JDK 17 o superior.
2. Ejecutar la clase `com.imvh.sigdeh.Main`.
3. Si los acentos se ven mal en la consola, agregar `-Dfile.encoding=UTF-8` a las opciones de la VM.

## Menú
1. Registrar solicitud
2. Actualizar solicitud
3. Consultar estado de una solicitud
4. Buscar solicitud por DNI
5. Listar solicitudes registradas
6. Emitir listado de prioridad
7. Salir

## Datos de prueba
Al iniciar se cargan cuatro grupos familiares, una operatoria abierta (con un universo general y uno de discapacidad) y un esquema de priorización con tres criterios: antigüedad de residencia, ingreso per cápita y vulnerabilidad. Se puede probar directamente la opción 6.

## Autor
Nahuel Ezequiel d'Almeida – Seminario de Práctica de Informática, Universidad Siglo 21.