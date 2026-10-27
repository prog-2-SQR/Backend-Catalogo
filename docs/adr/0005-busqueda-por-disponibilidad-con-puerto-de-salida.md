# ADR 0005: Búsqueda por disponibilidad con un puerto de salida hacia los horarios

- **Estado:** aceptada
- **Fecha:** 2026-10-27
- **Issue:** #31

## Contexto

La búsqueda de profesionales (#29) vive en el slice `professional` y filtra por categoría, nombre y estado habilitado, que son columnas propias. El enunciado (§4.1) también pide filtrar por **disponibilidad**, y eso depende de los horarios semanales, que son del slice `schedule`.

Es una dependencia entre slices: según la skill `hexagonal-arch`, la decide el dueño del proyecto.

## Decisión

La misma opción que en `sync` (ADR 0004), **puerto de salida** (decisión de Santi, 27/10):

- `professional/domain/ports/out/ProfessionalAvailability`: "qué profesionales atienden tal día de la semana" (`Set<Long>`).
- `SearchProfessionalsUseCaseImpl`: si la búsqueda trae `dayOfWeek`, pide esos IDs y limita la búsqueda a ellos (`ProfessionalSearch.restrictedTo`). La base filtra con `id IN (…)` junto con el resto de los filtros, así que la paginación sigue siendo correcta.
- `professional/infrastructure/schedule/WeeklyScheduleAvailabilityAdapter`: **único** archivo de `professional` que conoce a `schedule`; usa su API pública (`WeeklyScheduleService`).
- La regla "este horario atiende ese día" es del modelo de horarios: `WeeklySchedule.isActiveOn(DayOfWeek)` (mismo día y habilitado).

Disponibilidad se interpreta como **atención semanal** ("atiende los lunes"). La disponibilidad de turnos concretos (slots libres en una fecha, descontando ocupaciones y holds) es responsabilidad del servicio de turnos (enunciado §7).

## Alternativas consideradas

| Opción | Por qué no |
|---|---|
| Un slice `search` con una consulta que une las tablas de profesionales y horarios | Un slice leyendo tablas ajenas; los cambios de esquema de `schedule` romperían la búsqueda sin aviso. |
| `professional` usa directamente `WeeklyScheduleService` en su caso de uso | La capa de aplicación de `professional` quedaría acoplada a otro slice. |
| Copiar los días de atención en la tabla de profesionales | Duplica datos que hay que mantener sincronizados en cada cambio de horario. |

## Consecuencias

**A favor**

- El dominio y la aplicación de `professional` siguen sin conocer a `schedule`; el acople está en un solo adapter.
- La regla de disponibilidad está en el modelo (`isActiveOn`) y se testea sin base de datos.

**En contra**

- El adapter lee todos los horarios y filtra en memoria. Con el catálogo de la cátedra (44 horarios) es despreciable; si creciera mucho, `schedule` podría exponer una consulta por día sin cambiar el puerto.

## Referencias

- Enunciado §4.1 y §7; skill `hexagonal-arch` (dependencias entre slices); ADR 0003 y ADR 0004.
