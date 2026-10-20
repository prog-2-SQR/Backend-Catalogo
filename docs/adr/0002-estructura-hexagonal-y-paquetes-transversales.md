# ADR 0002: Estructura hexagonal y paquetes transversales

- **Estado:** aceptada
- **Fecha:** 2026-10-20
- **Issue:** #15

## Contexto

La cátedra pide arquitectura hexagonal con vertical slicing, según la skill `hexagonal-arch` del profesor: cada funcionalidad es un slice con `domain/`, `application/` e `infrastructure/`, el dominio no depende de frameworks y un slice no importa clases de otro.

Algunas piezas no pertenecen a ningún slice: el formato común de los errores HTTP, la configuración de la cátedra (URL, JWT técnico, Redis, Kafka) y, más adelante, la seguridad. El anexo §13 exige que todos los errores salgan en `application/problem+json` con un `code` estable.

## Decisión

**Paquetes del servicio** (`ar.edu.um.prog2.quiroga.catalogo`):

```
catalogo/
├── category/, professional/, schedule/   ← slices (domain / application / infrastructure)
├── sync/, search/                        ← slices que llegan en M2–M4
├── config/                               ← configuración de la cátedra (fuera de los slices)
└── shared/
    ├── web/                              ← formato de errores y handler global
    └── security/                         ← seguridad y JWT (M5)
```

- Los slices cuelgan directo del paquete del servicio, sin un segmento `hexagonal` intermedio.
- Dentro de `infrastructure/`, cada adaptador va en un paquete con el nombre de lo que contiene: `persistence/`, `web/`, `client/` (REST), `redis/`, `messaging/` (Kafka).
- **Regla de dependencias de lo transversal:** `shared` y `config` no tienen capas y **no importan ningún slice**. Los slices pueden usarlos solo desde su `infrastructure/`; `domain/` y `application/` siguen sin depender de nada externo.

**Errores HTTP:**

- `shared/web/ApiProblems` arma el `problem+json` con su `code`.
- Cada slice traduce **sus** excepciones en un `@RestControllerAdvice` propio, dentro de su `infrastructure/web/handler/` (por ejemplo, `CategoryException` → `404 CATEGORY_NOT_FOUND`). Tiene prioridad (`@Order(HIGHEST_PRECEDENCE)`).
- `shared/web/GlobalExceptionHandler` cubre lo que no es de ningún slice: errores estándar de Spring MVC (`400 VALIDATION_ERROR`, `404 NOT_FOUND`, etc.) y cualquier error no previsto (`500 INTERNAL_ERROR`, con un mensaje genérico; el detalle queda solo en el log).
- Los controllers no tienen `try/catch`: la excepción no chequeada sube y la traduce el handler.

## Alternativas consideradas

| Opción | Por qué no |
|---|---|
| Un único handler global que conozca las excepciones de todos los slices | `shared` importaría slices: rompe la regla de que lo transversal no conoce a los slices. |
| Una excepción base común en `shared` que extiendan las de cada slice | `application/` importaría `shared`; la skill permite en esa capa solo el dominio, anotaciones de Spring y Lombok. |
| `try/catch` + `ResponseStatusException` en cada controller (ejemplo de clase) | Repite código en cada endpoint y no permite agregar el `code` estable que pide el anexo. |
| Configuración y seguridad dentro de un slice | El profesor indicó ponerlas fuera de los slices (configuración en `config/`; transversales en la raíz o en `shared`). |

## Consecuencias

**A favor**

- Formato de error uniforme en todo el servicio, con `status` + `code` como pide el anexo.
- Los controllers quedan en una línea por endpoint (menor acoplamiento).
- Ningún error inesperado expone mensajes internos al cliente.
- Cada slice sigue siendo autocontenido: agregar uno nuevo no obliga a tocar `shared`.

**En contra**

- Cada slice necesita su propio handler (una clase chica por slice).
- El orden entre handlers depende de `@Order`; está cubierto por tests (`GlobalExceptionHandlerTest` y los tests de cada controller verifican el `code`).

## Referencias

- Skill `hexagonal-arch`: reglas no negociables y dependencias entre capas.
- Slack: respuestas del profesor sobre configuración de la cátedra (paquete `config` fuera de los slices), transversales (raíz o `shared`) y nombres de paquetes de adaptadores.
- ADR 0003: referencias entre slices por ID.
