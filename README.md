<!-- calidad:inicio -->
![Calidad](https://img.shields.io/badge/Calidad-54%2F100-yellow) ![Aprobar](https://img.shields.io/badge/Aprobar-s%C3%AD-brightgreen) ![Destacar](https://img.shields.io/badge/Destacar-no-lightgrey) ![Build](https://img.shields.io/badge/Build-OK-green) ![Duplicación](https://img.shields.io/badge/Duplicaci%C3%B3n-0.0%25-green) ![%POO](https://img.shields.io/badge/%25POO-54%25-orange) ![Sueltas](https://img.shields.io/badge/Sueltas-0-green) ![D1](https://img.shields.io/badge/D1-39-red) ![D2](https://img.shields.io/badge/D2-100-green) ![D4](https://img.shields.io/badge/D4-0-red) ![D5](https://img.shields.io/badge/D5-100-green) ![D6](https://img.shields.io/badge/D6-5-red) ![D7](https://img.shields.io/badge/D7-100-green)

**Calidad (heurístico, 7 dimensiones):** índice **54/100** · build **OK** · aprobar **sí (7/11)** · destacar **no**
Detalle completo en [`docs/EVALUACION.md`](docs/EVALUACION.md).
<!-- calidad:fin -->

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

# 1. Configuración local: copiar el ejemplo, poner una password propia en DB_PASSWORD,
#    los datos de la cuenta técnica de la cátedra (CATEDRA_*, REDIS_*, KAFKA_*) y el mismo JWT_BASE64_SECRET que turnos
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

## Seguridad

Todas las APIs (`/api/**`) requieren el JWT que devuelve el servicio de turnos en `POST /api/authenticate`, enviado como `Authorization: Bearer <id_token>`; sin token responden `401`. Son públicos `/actuator/health` y la documentación OpenAPI (`/v3/api-docs`, `/swagger-ui.html`).

## Tests

```bash
./mvnw verify
```

Los tests levantan su propio PostgreSQL, Redis y Kafka descartables con **Testcontainers**: no necesitan el `.env` ni el compose, solo Docker funcionando.

## Configuración

| Variable | Por defecto | Descripción |
|---|---|---|
| `DB_HOST` | `localhost` | Host de PostgreSQL |
| `DB_PORT` | `5432` | Puerto de PostgreSQL |
| `DB_NAME` | `catalogo` | Nombre de la base |
| `DB_USERNAME` | `catalogo` | Usuario |
| `DB_PASSWORD` | (obligatoria) | Password. Sin valor por defecto: se define en `.env` |
| `CATEDRA_API_URL` | (obligatoria) | URL base de la API REST de la cátedra (llega por ZeroTier) |
| `CATEDRA_JWT` | (obligatoria) | JWT técnico de la cuenta del proyecto. Nunca se commitea |
| `REDIS_HOST` / `REDIS_PORT` | `localhost` / `6379` | Redis de la cátedra (solo lectura) |
| `REDIS_USERNAME` / `REDIS_PASSWORD` | (de la integración) | Usuario y password de Redis que informa `GET /api/student/integration` |
| `KAFKA_BOOTSTRAP_SERVERS` | `localhost:9092` | Broker de Kafka de la cátedra |
| `KAFKA_CONSUMER_GROUP_ID` | `catalogo` | Consumer group de la integración (`alumnos-<groupId>`) |
| `KAFKA_CATALOG_TOPIC` | (obligatoria) | Topic de avisos de versión nueva (`catedra.catalog.<groupId>`) |
| `JWT_BASE64_SECRET` | (obligatoria) | Secreto de los JWT de los usuarios: **el mismo valor que en el servicio de turnos**, que los emite. El catálogo solo los valida |

Si falta una variable obligatoria de la cátedra, la aplicación no arranca y el error indica cuál. Los timeouts hacia la cátedra se pueden ajustar con `catedra.connect-timeout` (por defecto `5s`) y `catedra.read-timeout` (por defecto `10s`). Si la cátedra no responde o devuelve un 5xx, se reintenta hasta `catedra.max-retries` veces (por defecto `2`), esperando `catedra.retry-delay` (por defecto `1s`) y el doble en cada intento.

Al arrancar con la base vacía, el servicio carga el catálogo completo desde el snapshot de la cátedra. Si la cátedra no responde, arranca igual y lo informa en el log. Para no sincronizar al arrancar: `catedra.sync-on-startup=false`.

Después, cada minuto (`catedra.sync-interval`, formato ISO-8601, por defecto `PT1M`) revisa en el Redis de la cátedra si hay versiones nuevas y aplica los cambios en orden; si la versión local quedó fuera de la ventana o falta una versión, hace una sincronización completa. Se desactiva con `catedra.sync-polling=false`.

Además escucha el topic `KAFKA_CATALOG_TOPIC`: cada aviso `CatalogUpdated` dispara esa misma sincronización sin esperar al próximo chequeo. Un aviso repetido (mismo `eventId`) no se vuelve a procesar, y el offset se confirma después de aplicar los cambios. Si la sincronización falla, el aviso se reintenta (`catedra.max-retries`, `catedra.retry-delay`); si sigue fallando, el chequeo periódico recupera el atraso, porque Kafka solo avisa y los datos salen de Redis o del snapshot.

El estado de la sincronización se consulta con `GET /api/sync/status` (con JWT): versión local y cuándo se aplicó, versión publicada por la cátedra, último intento correcto y último error con su mensaje, y un resumen en `health` (`NOT_INITIALIZED`, `FAILING`, `UNKNOWN`, `OUTDATED` o `UP_TO_DATE`). Los intentos se registran en memoria: al reiniciar el servicio empiezan de nuevo, mientras que la versión local queda en la base.

```json
{"health": "UP_TO_DATE", "localVersion": 1, "appliedAt": "2026-10-28T11:29:41Z", "publishedVersion": 1,
 "lastSuccessAt": "2026-10-28T11:30:41Z", "lastFailureAt": null, "lastFailureMessage": null}
```

## Stack

| Componente | Versión |
|---|---|
| Java | 25 |
| Spring Boot | 4.1.1 |
| Maven (wrapper) | 3.10.0 |
| PostgreSQL | 18 |
| Liquibase | gestionado por Spring Boot |
| Dependencias | Spring Web MVC, Validation, Spring Data JPA, Spring Data Redis, Spring Kafka, Spring Security (resource server), RestClient, Lombok, Actuator |
| Tests | JUnit 5, Mockito, AssertJ, Testcontainers (PostgreSQL, Redis, Kafka), WireMock, JaCoCo |

## Decisiones de arquitectura

- [ADR 0001: Motor de base de datos](docs/adr/0001-motor-de-base-de-datos.md)
- [ADR 0002: Estructura hexagonal y paquetes transversales](docs/adr/0002-estructura-hexagonal-y-paquetes-transversales.md)
- [ADR 0003: Referencias entre slices por ID](docs/adr/0003-referencias-entre-slices-por-id.md)
- [ADR 0004: sync escribe en los slices del catálogo por un puerto de salida](docs/adr/0004-sync-escribe-en-los-slices-por-puerto-de-salida.md)
- [ADR 0005: Búsqueda por disponibilidad con un puerto de salida hacia los horarios](docs/adr/0005-busqueda-por-disponibilidad-con-puerto-de-salida.md)

## Repositorios del proyecto

- [Backend-Catalogo](https://github.com/prog-2-SQR/Backend-Catalogo): este servicio
- [Backend-Turnos](https://github.com/prog-2-SQR/Backend-Turnos): usuarios, disponibilidad y reservas
- [Frontend-KMP](https://github.com/prog-2-SQR/Frontend-KMP): app Android con Kotlin Multiplatform
- [Documentacion](https://github.com/prog-2-SQR/Documentacion): casos de uso, diagramas y decisiones de arquitectura
