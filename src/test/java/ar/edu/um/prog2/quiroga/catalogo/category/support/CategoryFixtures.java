package ar.edu.um.prog2.quiroga.catalogo.category.support;

import ar.edu.um.prog2.quiroga.catalogo.category.domain.model.Category;
import java.time.Instant;

/** Datos de prueba basados en el snapshot real de la cátedra. */
public final class CategoryFixtures {

    public static final Instant CREATED_AT = Instant.parse("2026-09-01T03:00:00Z");
    public static final Instant UPDATED_AT = Instant.parse("2026-09-10T13:40:00Z");

    private CategoryFixtures() {
    }

    public static Category cardiologia() {
        return Category.builder()
                .id(101L)
                .name("Cardiologia")
                .description("Atencion cardiovascular")
                .enabled(true)
                .createdAt(CREATED_AT)
                .updatedAt(UPDATED_AT)
                .build();
    }

    public static Category pediatria() {
        return Category.builder()
                .id(102L)
                .name("Pediatria")
                .description("Atencion pediatrica")
                .enabled(false)
                .createdAt(CREATED_AT)
                .updatedAt(UPDATED_AT)
                .build();
    }
}
