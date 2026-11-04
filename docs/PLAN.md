# PLAN: Backend-Catalogo

Cómo se construyó el servicio que describe [SPEC.md](SPEC.md): arquitectura, orden de trabajo y decisiones. Las tareas una por una están en [TASKS.md](TASKS.md).

## Arquitectura

Hexagonal con *vertical slicing*, según el modelo del profesor ([dqmdz/hexagonal](https://github.com/dqmdz/hexagonal)), sin segmento intermedio ([ADR 0002](adr/0002-estructura-hexagonal-y-paquetes-transversales.md)). Cada slice tiene tres capas:

- `domain/`: modelo, puertos (`ports/in`, un caso de uso por interfaz; `ports/out`, lo que necesita de afuera) y su excepción. Solo usa `java.*` y Lombok, sin Spring, JPA ni Jackson.
- `application/`: la implementación de cada caso de uso y un service que hace de fachada.
- `infrastructure/`: los adaptadores, que son controllers, JPA, cliente REST de la cátedra (`client/`), Redis (`redis/`), Kafka (`messaging/`) y tareas programadas (`scheduling/`), con sus DTO y mappers escritos a mano.

```
ar.edu.um.prog2.quiroga.catalogo
├── config/          propiedades de la cátedra, RestClient con timeouts y reintentos, Kafka y scheduling
├── shared/web/      handler global de errores (problem+json)
├── shared/security/ validación del JWT de los usuarios
├── category/        categorías
├── professional/    profesionales y búsqueda con filtros
├── schedule/        horarios semanales
└── sync/            snapshot, incremental desde Redis, aviso por Kafka y estado de la sincronización
```

- **Entre slices**:
  - `professional` y `schedule` guardan el id de la categoría y del profesional, sin FK ([ADR 0003](adr/0003-referencias-entre-slices-por-id.md));
  - `sync` escribe en los demás por un puerto de salida que llama a sus casos de uso ([ADR 0004](adr/0004-sync-escribe-en-los-slices-por-puerto-de-salida.md));
  - la búsqueda por día consulta los horarios por otro puerto ([ADR 0005](adr/0005-busqueda-por-disponibilidad-con-puerto-de-salida.md)).
- **Transversales**: `config` y `shared` están fuera de los slices y no los importan (decisión del profesor en Slack).

## Etapas

| Milestone | Objetivo | Resultado |
|---|---|---|
| M0 · Setup e infraestructura | Proyecto base, PostgreSQL con Liquibase, CI y SonarQube Cloud | Spring Boot 4.1 con Java 25, compose, `ci.yml` y quality gate |
| M1 · Catálogo: modelo local | Los tres slices del catálogo con su API de consulta | Categorías, profesionales y horarios, problem+json y JaCoCo |
| M2 · Catálogo: sincronización completa | Cargar el catálogo desde la cátedra | Configuración externa, cliente del snapshot y aplicación transaccional al arrancar |
| M3 · Catálogo: sincronización incremental | Mantenerlo al día | Lectura de Redis, aplicación versión por versión, aviso por Kafka y estado de la sincronización |
| M4 · Catálogo: búsqueda y contrato interno | Búsqueda para la app y consultas de Turnos | Filtros por categoría, nombre, habilitado y día, con paginación |
| M5 · Usuarios y seguridad | Proteger las APIs | Validación del JWT que emite Turnos |
| M9 · Documentación y evidencias | Entrega reproducible y documentada | Dockerfile, SPEC/PLAN/TASKS, ADRs y compose de toda la solución (en Documentacion) |

## Decisiones

| ADR | Decisión |
|---|---|
| [0001](adr/0001-motor-de-base-de-datos.md) | PostgreSQL con Liquibase |
| [0002](adr/0002-estructura-hexagonal-y-paquetes-transversales.md) | Estructura hexagonal por slices, con `config` y `shared` afuera |
| [0003](adr/0003-referencias-entre-slices-por-id.md) | Referencias entre slices por id, sin FK |
| [0004](adr/0004-sync-escribe-en-los-slices-por-puerto-de-salida.md) | `sync` escribe en los slices por un puerto de salida |
| [0005](adr/0005-busqueda-por-disponibilidad-con-puerto-de-salida.md) | Búsqueda por día de atención con un puerto hacia los horarios |
| [0006](adr/0006-sincronizacion-completa-e-incremental.md) | Sincronización completa e incremental: transacción por versión, Kafka como aviso, chequeo periódico y deduplicación |
| [0007](adr/0007-validacion-del-jwt-de-los-usuarios.md) | Validación del JWT de los usuarios con el secreto compartido |

## Estrategia de pruebas

- **Casos de uso**: tests unitarios con Mockito sobre los puertos, que cubren el camino feliz y cada error.
- **Controllers**: MockMvc, que verifica la seguridad (`401` sin token), la validación y el formato problem+json.
- **Adaptadores**: JPA y la búsqueda contra PostgreSQL en Testcontainers; el snapshot contra WireMock (timeouts, `5xx` y reintentos); Redis y Kafka con contenedores reales.
- **Sincronización de punta a punta**: snapshot, cambios en orden, discontinuidad, falla a mitad de una versión y aviso repetido.
- **CI**: `./mvnw verify` en cada push y PR, con JaCoCo y SonarQube Cloud. Un PR se mergea solo con el CI en verde.

## Recortes y pendientes

- **Kafka de la cátedra**: hasta el 03/11 el topic `catedra.catalog.<groupId>` no publicaba avisos, y el chequeo periódico de Redis mantenía el catálogo al día. Desde el 04/11 la cátedra cambia cada hora el estado del primer profesional y publica el aviso: el catálogo lo aplica en segundos (probado contra la cátedra, versiones 2 a 6). Detalle en [Documentacion/integracion/pruebas-con-la-catedra.md](https://github.com/prog-2-SQR/Documentacion/blob/main/integracion/pruebas-con-la-catedra.md).
