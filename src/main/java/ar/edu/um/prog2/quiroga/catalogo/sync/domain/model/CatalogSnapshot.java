package ar.edu.um.prog2.quiroga.catalogo.sync.domain.model;

import java.time.Instant;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Estado del catálogo en una versión: completo (snapshot REST, anexo §7) o solo las entidades que cambiaron en esa
 * versión (Redis, anexo §14.5). En los dos casos se aplica igual: upsert de las entidades y la versión al final.
 * Modelo propio de sync: no importa los modelos de los otros slices (opción "puerto de salida" de la skill).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CatalogSnapshot {

    private Long snapshotVersion;
    private Instant generatedAt;
    @Builder.Default
    private List<CatalogCategory> categories = List.of();
    @Builder.Default
    private List<CatalogProfessional> professionals = List.of();
    @Builder.Default
    private List<CatalogWeeklySchedule> weeklySchedules = List.of();
}
