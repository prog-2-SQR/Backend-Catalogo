package ar.edu.um.prog2.quiroga.catalogo.professional.support;

import ar.edu.um.prog2.quiroga.catalogo.professional.domain.model.Professional;
import java.time.Instant;

/** Datos de prueba basados en el snapshot real de la cátedra. */
public final class ProfessionalFixtures {

    public static final Instant CREATED_AT = Instant.parse("2026-09-01T03:00:00Z");
    public static final Instant UPDATED_AT = Instant.parse("2026-09-10T13:40:00Z");

    private ProfessionalFixtures() {
    }

    public static Professional anaTorres() {
        return Professional.builder()
                .id(201L)
                .categoryId(101L)
                .firstName("Ana")
                .lastName("Torres")
                .enabled(true)
                .createdAt(CREATED_AT)
                .updatedAt(UPDATED_AT)
                .build();
    }

    public static Professional emilianoOrtiz() {
        return Professional.builder()
                .id(224L)
                .categoryId(105L)
                .firstName("Emiliano")
                .lastName("Ortiz")
                .enabled(false)
                .createdAt(CREATED_AT)
                .updatedAt(UPDATED_AT)
                .build();
    }
}
