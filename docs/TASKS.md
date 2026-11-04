# TASKS: Backend-Catalogo

Las tareas del [PLAN](PLAN.md), una issue por tarea y un PR por issue, mergeado a `main` después de la revisión. ✅ terminada.

## M0 · Setup e infraestructura

- ✅ #1 Inicializar el proyecto Spring Boot 4.1 con Java 25 (PR #2)
- ✅ #3 PostgreSQL, Liquibase y `compose.yaml` (PR #4)
- ✅ #36 Analizar la calidad con SonarQube Cloud (PR #37)
- ✅ #39 Corregir el hallazgo de SonarQube Cloud (PR #40)
- ✅ #41 Actualizar dependencias con vulnerabilidades conocidas (PR #42)

## M1 · Catálogo: modelo local

- ✅ #5 Dominio, casos de uso y persistencia de categoría (PR #6)
- ✅ #7 Endpoints de consulta de categorías (PR #8)
- ✅ #9 Tests del slice de categoría y cobertura con JaCoCo (PR #10)
- ✅ #11 Slice de profesionales (PR #12)
- ✅ #13 Slice de horarios semanales (PR #14)
- ✅ #15 Manejo global de errores en formato problem+json (PR #16)

## M2 · Catálogo: sincronización completa

- ✅ #17 Externalizar la configuración de acceso a la cátedra (PR #18)
- ✅ #19 Obtener el snapshot del catálogo desde la cátedra (PR #20)
- ✅ #21 Aplicar el snapshot de forma transaccional y cargarlo al arrancar (PR #22)

## M3 · Catálogo: sincronización incremental

- ✅ #23 Leer versiones y cambios desde el Redis de la cátedra (PR #24)
- ✅ #25 Aplicar los cambios incrementales desde Redis (PR #26)
- ✅ #33 Escuchar `CatalogUpdated` en Kafka (PR #34)
- ✅ #35 Informar el estado de la sincronización (PR #38)

## M4 · Catálogo: búsqueda y contrato interno

- ✅ #29 Buscar profesionales con filtros y paginación (PR #30)
- ✅ #31 Filtrar la búsqueda por disponibilidad semanal (PR #32)

## M5 · Usuarios y seguridad

- ✅ #27 Validar el JWT emitido por Turnos (PR #28)

## M9 · Documentación y evidencias

- ✅ #43 Imagen JVM de Kafka en el test del listener (PR #44)
- ✅ #45 Dockerfile del servicio (PR #46)
- ✅ #47 SPEC, PLAN, TASKS, ADRs de sincronización y seguridad, y README al día
- ✅ Compose de toda la solución: en [Documentacion](https://github.com/prog-2-SQR/Documentacion)
