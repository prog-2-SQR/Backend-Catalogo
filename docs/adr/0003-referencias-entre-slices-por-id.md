# ADR 0003: Referencias entre slices por ID

- **Estado:** aceptada
- **Fecha:** 2026-10-19
- **Issue:** #11

## Contexto

El servicio de catálogo está organizado en slices verticales (arquitectura hexagonal de la cátedra): `category`, `professional` y, más adelante, `schedule`. Cada profesional pertenece a una categoría, y cada horario semanal pertenece a un profesional.

La skill `hexagonal-arch` prohíbe que un slice importe clases internas de otro y pide que cualquier dependencia entre slices la decida el dueño del proyecto (opciones: copia propia, referencia por ID, puerto de salida hacia el otro slice, kernel compartido o unir los slices).

Los datos vienen de la cátedra ya relacionados por ID: en el snapshot y en Redis, un profesional trae `categoryId` y un horario trae `professionalId` (anexo §7 y §14.4).

## Decisión

**Los slices se relacionan solo por ID.** `Professional` guarda `Long categoryId`; el slice `professional` no importa nada del slice `category` (ni dominio, ni aplicación, ni infraestructura). El mismo criterio aplica a los horarios (`professionalId`).

En la base de datos, la columna `professionals.category_id` tiene un **índice** (para filtrar por categoría en la búsqueda) pero **no una clave foránea** hacia `categories`.

## Alternativas consideradas

| Opción | Por qué no |
|---|---|
| Copia propia (`professional` define su `CategoryRef` con id y nombre) | Duplica datos de la categoría que habría que mantener sincronizados en dos lugares. |
| Puerto de salida de `professional` hacia `category` | Agrega acoplamiento sin un caso de uso que lo necesite hoy: ninguna operación de profesionales tiene que validar o leer la categoría. Se puede incorporar si aparece ese caso. |
| Kernel compartido | La categoría no es un concepto transversal: tiene su propio slice. |
| Unir categoría y profesional en un solo slice | Son entidades distintas, con sus propias consultas y endpoints. |
| Clave foránea `professionals.category_id → categories.id` | Acopla los slices a nivel de base de datos y obliga a la sincronización a aplicar los datos en un orden fijo; la integridad la garantiza la cátedra, que es la fuente de los datos. |

## Consecuencias

**A favor**

- Los slices se pueden modificar y testear por separado; la regla de dependencias de la skill se cumple sin excepciones.
- El modelo local coincide con el contrato de la cátedra (`categoryId`), así que los mappers de sincronización son directos.
- La sincronización puede guardar un profesional aunque su categoría todavía no esté en la base (verificado en `JpaProfessionalRepositoryAdapterTest`).

**En contra**

- La base no impide un `category_id` que no exista. Se acepta porque los datos los publica la cátedra y se aplican en una misma unidad de trabajo por versión.
- Si la app necesita mostrar el nombre de la categoría junto al profesional, la composición se resuelve en el slice de búsqueda o en el cliente, no dentro de `professional`.
