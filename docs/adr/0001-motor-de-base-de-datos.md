# ADR 0001: Motor de base de datos

- **Estado:** aceptada
- **Fecha:** 2026-10-15
- **Issue:** #3

## Contexto

El servicio de catálogo necesita persistir su copia local del catálogo (categorías, profesionales y horarios semanales) junto con la versión aplicada y el estado de sincronización.

Restricciones del enunciado:

- Cada servicio es dueño exclusivo de sus datos y administra sus propias migraciones (§3).
- La persistencia principal debe usar un gestor de base de datos servidor: no se aceptan H2, SQLite ni bases embebidas o en memoria (§3).
- La infraestructura debe poder levantarse con Docker Compose (§3).
- El motor es decisión del alumno y debe justificarse (§10).

Además, el diseño de sincronización exige aplicar cada versión del catálogo como una unidad atómica: si falla un cambio, no puede quedar una mezcla de versiones (enunciado §6, anexo §14.5).

## Decisión

Usar **PostgreSQL 18** como base de datos del servicio, con migraciones gestionadas por **Liquibase**.

- Imagen `postgres:18-alpine`, la misma en `compose.yaml` y en los tests (Testcontainers), para que se comporten igual.
- Una instancia propia para este servicio, en su propio `compose.yaml`. El servicio de turnos tiene la suya: no comparten base ni usuario.
- Las credenciales llegan por variables de entorno; no hay valores secretos en el repositorio.

## Alternativas consideradas

| Opción | Por qué no |
|---|---|
| H2 / SQLite | Prohibidas por el enunciado como almacenamiento principal. |
| MySQL 8 | Cumple los requisitos y en lo básico es equivalente. PostgreSQL suma índices únicos parciales (MySQL no los tiene) y un tipo explícito para instantes con zona horaria (`timestamptz`). |
| Una sola instancia de PostgreSQL con dos bases (una por servicio) | El enunciado lo permite, pero obliga a separar usuarios y permisos a mano. Con una instancia por servicio, la separación de datos se cumple por construcción y cada repositorio se levanta de forma independiente. |

## Consecuencias

**A favor**

- Transacciones ACID robustas: cada versión del catálogo se aplica en una transacción y la versión local solo avanza si todo quedó aplicado.
- `timestamptz` guarda instantes en UTC, que es el formato que usa el anexo para fechas y horas.
- Funciones útiles para próximos milestones: índices únicos parciales (por ejemplo, "un solo proceso pendiente por usuario" en turnos) y `SELECT ... FOR UPDATE SKIP LOCKED` para procesar el outbox sin que dos hilos tomen el mismo evento.
- Soporte de primera clase en Liquibase, Testcontainers y Spring Boot (`@ServiceConnection`).

**En contra**

- Hace falta Docker para levantar la base y para correr los tests. Docker ya es requisito del proyecto (Compose y Testcontainers), así que no suma una dependencia nueva.
- Dos instancias de PostgreSQL (una por servicio) consumen más memoria en local que una sola.
