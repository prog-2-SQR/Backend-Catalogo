# Backend-Catalogo

[![CI](https://github.com/prog-2-SQR/Backend-Catalogo/actions/workflows/ci.yml/badge.svg)](https://github.com/prog-2-SQR/Backend-Catalogo/actions/workflows/ci.yml)

Servicio de **catálogo y sincronización** del proyecto integrador de Programación 2 (Universidad de Mendoza, 2026).

Mantiene una copia local de categorías, profesionales y horarios semanales publicada por la cátedra, la sincroniza (snapshot REST, cambios incrementales vía Redis y notificaciones Kafka) y la expone para búsquedas a la app KMP y al servicio de turnos.

## Estado

Esqueleto con base de datos: proyecto Spring Boot conectado a su propio PostgreSQL, con migraciones Liquibase y health check. La sincronización y las APIs se incorporan en las próximas issues (ver milestones del repositorio).

## Requisitos

- **JDK 25**
- **Docker** con Docker Compose (para la base de datos y para los tests)
- **Git**

No hace falta instalar Maven: el wrapper (`./mvnw`) descarga la versión correcta la primera vez.

## Cómo ejecutarlo

```bash
git clone https://github.com/prog-2-SQR/Backend-Catalogo.git
cd Backend-Catalogo

# 1. Configuración local: copiar el ejemplo y poner una password propia en DB_PASSWORD
cp .env.example .env

# 2. Levantar PostgreSQL (puerto 5432)
docker compose up -d

# 3. Levantar el servicio (puerto 8080)
./mvnw spring-boot:run
```

La aplicación lee el archivo `.env` al arrancar. Liquibase aplica las migraciones automáticamente.

Para comprobar que está vivo y conectado a la base:

```bash
curl http://localhost:8080/actuator/health
# {"components":{"db":{"status":"UP"}, ...},"status":"UP"}
```

Para detener la base sin perder los datos: `docker compose down` (los datos quedan en el volumen `catalogo-db-data`).

En Windows, usar `mvnw.cmd` en lugar de `./mvnw`.

## Tests

```bash
./mvnw verify
```

Los tests levantan su propio PostgreSQL descartable con **Testcontainers**: no necesitan el `.env` ni el compose, solo Docker funcionando.

## Configuración

| Variable | Por defecto | Descripción |
|---|---|---|
| `DB_HOST` | `localhost` | Host de PostgreSQL |
| `DB_PORT` | `5432` | Puerto de PostgreSQL |
| `DB_NAME` | `catalogo` | Nombre de la base |
| `DB_USERNAME` | `catalogo` | Usuario |
| `DB_PASSWORD` | (obligatoria) | Password. Sin valor por defecto: se define en `.env` |

## Stack

| Componente | Versión |
|---|---|
| Java | 25 |
| Spring Boot | 4.1.1 |
| Maven (wrapper) | 3.10.0 |
| PostgreSQL | 18 |
| Liquibase | gestionado por Spring Boot |
| Dependencias | Spring Web MVC, Validation, Spring Data JPA, Lombok, Actuator |
| Tests | JUnit 5, Testcontainers |

## Decisiones de arquitectura

- [ADR 0001: Motor de base de datos](docs/adr/0001-motor-de-base-de-datos.md)

## Repositorios del proyecto

- [Backend-Catalogo](https://github.com/prog-2-SQR/Backend-Catalogo): este servicio
- [Backend-Turnos](https://github.com/prog-2-SQR/Backend-Turnos): usuarios, disponibilidad y reservas
- [Frontend-KMP](https://github.com/prog-2-SQR/Frontend-KMP): app Android con Kotlin Multiplatform
- [Documentacion](https://github.com/prog-2-SQR/Documentacion): casos de uso, diagramas y decisiones de arquitectura
