# Backend-Catalogo

[![CI](https://github.com/prog-2-SQR/Backend-Catalogo/actions/workflows/ci.yml/badge.svg)](https://github.com/prog-2-SQR/Backend-Catalogo/actions/workflows/ci.yml)

Servicio de **catálogo y sincronización** del proyecto integrador de Programación 2 (Universidad de Mendoza, 2026).

Mantiene una copia local de categorías, profesionales y horarios semanales publicada por la cátedra, la sincroniza (snapshot REST, cambios incrementales vía Redis y notificaciones Kafka) y la expone para búsquedas a la app KMP y al servicio de turnos.

## Estado

Esqueleto inicial: proyecto Spring Boot que compila, arranca y expone el health check. La base de datos, la sincronización y las APIs se incorporan en las próximas issues (ver milestones del repositorio).

## Requisitos

- **JDK 25**
- **Git**

No hace falta instalar Maven: el wrapper (`./mvnw`) descarga la versión correcta la primera vez.

## Cómo ejecutarlo

```bash
git clone https://github.com/prog-2-SQR/Backend-Catalogo.git
cd Backend-Catalogo

# Compilar y correr los tests
./mvnw verify

# Levantar el servicio (puerto 8080)
./mvnw spring-boot:run
```

Para comprobar que está vivo:

```bash
curl http://localhost:8080/actuator/health
# {"groups":["liveness","readiness"],"status":"UP"}
```

En Windows, usar `mvnw.cmd` en lugar de `./mvnw`.

## Stack

| Componente | Versión |
|---|---|
| Java | 25 |
| Spring Boot | 4.1.1 |
| Maven (wrapper) | 3.10.0 |
| Dependencias | Spring Web MVC, Validation, Lombok, Actuator |

## Repositorios del proyecto

- [Backend-Catalogo](https://github.com/prog-2-SQR/Backend-Catalogo): este servicio
- [Backend-Turnos](https://github.com/prog-2-SQR/Backend-Turnos): usuarios, disponibilidad y reservas
- [Frontend-KMP](https://github.com/prog-2-SQR/Frontend-KMP): app Android con Kotlin Multiplatform
- [Documentacion](https://github.com/prog-2-SQR/Documentacion): casos de uso, diagramas y decisiones de arquitectura
