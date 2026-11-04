# ADR 0007: Validación del JWT de los usuarios

- **Estado:** aceptada
- **Fecha:** 2026-11-03
- **Issue:** #27 (la decisión se tomó ahí; este ADR la deja registrada)

## Contexto

El enunciado (§2 y §9) pide que los usuarios usen un JWT para todo lo protegido y que los secretos no queden en el código. Los usuarios se registran e inician sesión en el servicio de turnos, que emite el JWT (Backend-Turnos, ADR 0007). La app consulta el catálogo directamente para buscar profesionales, y Turnos también lo consulta para calcular la disponibilidad.

El catálogo maneja dos credenciales:
- el **JWT de los usuarios**, que tiene que aceptar;
- el **JWT técnico de la cátedra**, con el que pide el snapshot. Ese token no tiene que salir del servicio.

## Decisión

- El catálogo es un **resource server**: valida el JWT de los usuarios con `oauth2-resource-server` y el mismo `JWT_BASE64_SECRET` (HS512) con el que lo firma Turnos. No emite tokens ni tiene usuarios propios.
- **Todo `/api/**` exige un JWT válido**, y sin él responde `401`. Son públicos `/actuator/health` y la documentación OpenAPI (`/v3/api-docs`, `/swagger-ui.html`).
- **Sin sesiones**: `SessionCreationPolicy.STATELESS` y CSRF desactivado.
- **Turnos llama con el JWT del usuario** (*token relay*): el catálogo no distingue si el pedido viene de la app o de Turnos, y no necesita otra credencial para eso (Backend-Turnos, ADR 0003).
- El **JWT de la cátedra** (`CATEDRA_JWT`) solo se usa en el `RestClient` hacia la cátedra. No se loguea ni se devuelve.
- Secretos (`JWT_BASE64_SECRET`, `CATEDRA_JWT`, `REDIS_PASSWORD`, `DB_PASSWORD`) solo por variables de entorno, con `.env` ignorado por git y `.env.example` con valores falsos. Sin ellos la app no arranca.
- Los errores salen como `application/problem+json`, sin trazas ni mensajes internos.

## Alternativas consideradas

- **Dejar el catálogo público**: es información de solo lectura, pero el enunciado pide JWT para las funcionalidades protegidas, y la búsqueda es parte de la app.
- **Clave pública RSA** emitida por Turnos: evita compartir un secreto, pero suma manejo de claves. Con dos servicios propios, el secreto compartido alcanza.
- **Credencial propia de Turnos hacia el catálogo**: obligaría a mantener otra credencial, y el catálogo perdería de vista al usuario del pedido.

## Consecuencias

- Los dos servicios tienen que tener el mismo `JWT_BASE64_SECRET`. El compose de Documentacion lo pasa a los dos desde un solo `.env`.
- El catálogo no puede emitir tokens: depende de Turnos para el login.
- Los tests de controllers verifican el `401` sin token, y el test de seguridad, que se acepta un token firmado con el secreto y se rechaza uno firmado con otro.
