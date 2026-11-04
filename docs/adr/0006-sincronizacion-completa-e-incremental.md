# ADR 0006: Sincronización completa e incremental

- **Estado:** aceptada
- **Fecha:** 2026-11-03
- **Issues:** #19, #21, #23, #25, #33, #35 (las decisiones se tomaron ahí; este ADR las reúne)
- **Diagrama:** [Documentacion/diagramas/sincronizacion.md](https://github.com/prog-2-SQR/Documentacion/blob/main/diagramas/sincronizacion.md)

## Contexto

La cátedra publica el catálogo (categorías, profesionales y horarios) por tres vías (enunciado §6; anexo §6, §14 y §15.2):
- **REST**: el snapshot completo, con su `snapshotVersion`;
- **Redis**: la metadata de versiones (`currentVersion`, `oldestAvailableVersion`), los ids que cambiaron en cada versión (`changes:{v}`) y el estado vigente de cada entidad;
- **Kafka**: un aviso `CatalogUpdated` cada vez que hay una versión nueva.

El enunciado pide aplicar los cambios en orden, que la versión local avance solo cuando la unidad de trabajo terminó bien, que procesar dos veces lo mismo no cambie el resultado y que se detecte cuándo no se puede seguir incrementalmente (§6.2 y §8). Kafka puede duplicar, perder o desordenar avisos, y Redis solo guarda una ventana de versiones.

## Decisión

1. **Snapshot al arrancar con la base vacía**, y cada vez que no se puede seguir incrementalmente. El snapshot reemplaza todo el catálogo y guarda su versión **en una sola transacción**: o queda el catálogo nuevo completo, o queda el anterior.
2. **Incremental desde Redis, versión por versión.** Desde `local + 1` hasta `currentVersion`, se leen los ids de `changes:{v}` y el estado vigente de cada uno, y se aplican **en una transacción por versión**. La versión local (`sync_state`) se guarda en esa misma transacción.
3. **Kafka solo dispara.** Un `CatalogUpdated` arranca la sincronización incremental, pero los datos salen siempre de Redis. Además, cada minuto se revisa Redis aunque no llegue ningún aviso (`catedra.sync-interval`).
4. **Discontinuidad → snapshot**: si la versión local es menor que `oldestAvailableVersion`, o falta un `changes:{v}`, o su JSON no se puede leer.
5. **Idempotencia en dos niveles**: el `eventId` de cada aviso se guarda en `sync_processed_events`, en la misma transacción, y un aviso repetido se descarta. Además, un aviso con `newVersion` menor o igual a la local no hace nada.
6. **Offset de Kafka después de procesar** (`ack-mode: record`). Si la sincronización falla, el aviso se reintenta hasta `catedra.max-retries` veces, y si sigue fallando, el chequeo periódico recupera el atraso.
7. **`sync` no escribe en las tablas de otros slices**: llama a sus casos de uso `Upsert*` por un puerto de salida ([ADR 0004](0004-sync-escribe-en-los-slices-por-puerto-de-salida.md)).

## Alternativas consideradas

- **Tomar los datos del mensaje de Kafka**: el aviso no trae los cambios, y depender de su orden y entrega sería frágil. Redis tiene el estado vigente.
- **Solo Kafka, sin chequeo periódico**: un aviso perdido dejaría el catálogo atrasado hasta el próximo. El chequeo cada minuto acota el atraso.
- **Una transacción para todas las versiones pendientes**: si falla la última, se pierden las anteriores que estaban bien. Con una por versión, se avanza todo lo posible.
- **Snapshot periódico siempre**: más simple, pero trae todo el catálogo aunque haya cambiado una fila, y no aprovecha el incremental que pide el enunciado.

## Consecuencias

- El catálogo local siempre corresponde exactamente a una versión publicada, nunca a una mezcla.
- Un aviso duplicado, perdido o fuera de orden no cambia el resultado: lo que manda es la versión local y lo que hay en Redis.
- Si la cátedra no responde, las búsquedas siguen con la copia local, y el error queda visible en `GET /api/sync/status` (#35).
- Probado con Testcontainers: snapshot, cambios en orden, discontinuidad, aviso repetido y falla a mitad de una versión. Contra la cátedra real, el topic de Kafka no publica avisos, y el chequeo periódico de Redis es el que mantiene el catálogo al día.
