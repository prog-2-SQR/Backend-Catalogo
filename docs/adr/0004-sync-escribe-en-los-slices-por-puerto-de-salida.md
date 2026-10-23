# ADR 0004: sync escribe en los slices del catálogo por un puerto de salida

- **Estado:** aceptada
- **Fecha:** 2026-10-23
- **Issue:** #21

## Contexto

El slice `sync` obtiene el catálogo de la cátedra (snapshot REST, y más adelante cambios por Redis y Kafka) y tiene que guardarlo en la copia local. Esa copia está repartida en tres slices: `category`, `professional` y `schedule`, cada uno dueño de su tabla.

La skill `hexagonal-arch` prohíbe que un slice use clases internas de otro y pide que cualquier dependencia entre slices la decida el dueño del proyecto. Las opciones que plantea: copia propia, referencia por ID, puerto de salida hacia el otro slice, kernel compartido o unir los slices.

Además, el enunciado (§6.1) y el anexo (§7) exigen que el snapshot se aplique de forma consistente: la versión local solo cambia si las tres colecciones quedaron guardadas.

## Decisión

Opción **puerto de salida** (decisión de Santi, 23/10):

- `sync` tiene su **propio modelo** (`CatalogSnapshot`, `CatalogCategory`, `CatalogProfessional`, `CatalogWeeklySchedule`, `SyncState`). Su `domain/` y su `application/` no importan nada de otros slices.
- `sync/domain/ports/out/CatalogWriter` declara lo que `sync` necesita: guardar una categoría, un profesional o un horario.
- Un **único adapter**, `sync/infrastructure/catalog/CatalogSlicesWriterAdapter`, lo implementa llamando a la API pública de cada slice (`CategoryService`, `ProfessionalService`, `WeeklyScheduleService`, método `upsert…`). Nunca usa sus repositorios ni su infraestructura.
- `ApplySnapshotUseCase` aplica las tres colecciones y después guarda la versión en `sync_state`, todo dentro de **una transacción** (`@Transactional`). Si algo falla, se deshace todo.
- La llamada HTTP a la cátedra queda **fuera** de la transacción (`SynchronizeSnapshotUseCase` pide el snapshot y después llama al caso de uso transaccional), para no tener una conexión a la base abierta mientras se espera la respuesta.

La dependencia va en un solo sentido (`sync` → otros slices); ningún slice del catálogo conoce a `sync`.

## Alternativas consideradas

| Opción | Por qué no |
|---|---|
| `sync` importa en su `application/` los modelos y casos de uso de los otros slices | Menos clases, pero la capa de aplicación de `sync` quedaría acoplada a otros slices, y la regla de la skill dejaría de cumplirse donde más se nota. |
| `sync` escribe directo en las tablas (sus propios repositorios JPA sobre `categories`, `professionals`…) | Dos slices dueños de la misma tabla; los cambios de esquema de un slice romperían al otro sin aviso. |
| Unir `category`, `professional`, `schedule` y `sync` en un slice `catalog` | Rompe los tres slices ya mergeados con sus tests y el ADR 0003; mezcla consultas públicas con la integración. |
| Kernel compartido con un modelo único de catálogo | El catálogo no es un concepto transversal: cada entidad ya tiene su slice. |

## Consecuencias

**A favor**

- El dominio y la aplicación de `sync` se testean sin los otros slices (mocks de `CatalogWriter` y `SyncStateRepository`).
- El acoplamiento entre slices queda en un solo archivo, fácil de encontrar y de cambiar.
- Los cambios incrementales de M3 (Redis/Kafka) reutilizan el mismo modelo y el mismo puerto.
- La consistencia está probada contra PostgreSQL real: un horario inválido a mitad del snapshot deja la base vacía y sin versión (`SnapshotSynchronizationTest`).

**En contra**

- Los campos de cada entidad aparecen en el modelo de `sync`, en sus DTOs del cliente y en el modelo del slice dueño, con un mapper entre cada par.
- Si un slice cambia la firma de su `upsert…`, hay que ajustar el adapter de `sync`.

## Referencias

- Skill `hexagonal-arch`: "Cross-Slice Dependencies (Ask Before Deciding)", opción *Outbound port to the other slice*.
- Enunciado §6.1; anexo §7 ("`snapshotVersion` solo se guarda como version local cuando las tres colecciones quedaron aplicadas correctamente").
- ADR 0003: referencias entre slices por ID.
