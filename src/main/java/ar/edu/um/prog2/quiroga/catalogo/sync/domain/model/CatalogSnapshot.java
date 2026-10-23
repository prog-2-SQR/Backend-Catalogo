package ar.edu.um.prog2.quiroga.catalogo.sync.domain.model;

import java.time.Instant;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Copia completa del catálogo publicada por la cátedra (anexo §7).
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
