package ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.client.dto;

import java.time.Instant;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Respuesta de GET /api/synchronization/snapshot (anexo §7), con los nombres del JSON de la cátedra. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CatalogSnapshotResponse {

    private Long snapshotVersion;
    private Instant generatedAt;
    private List<CatalogCategoryResponse> professionalCategories;
    private List<CatalogProfessionalResponse> professionals;
    private List<CatalogWeeklyScheduleResponse> weeklySchedules;
}
