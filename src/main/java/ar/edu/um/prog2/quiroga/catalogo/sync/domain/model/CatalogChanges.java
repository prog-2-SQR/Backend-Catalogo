package ar.edu.um.prog2.quiroga.catalogo.sync.domain.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Qué entidades cambiaron en una versión (anexo §14.5): solo los IDs; los datos se leen del estado vigente. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CatalogChanges {

    private Long version;
    @Builder.Default
    private List<Long> categoryIds = List.of();
    @Builder.Default
    private List<Long> professionalIds = List.of();
    @Builder.Default
    private List<Long> weeklyScheduleIds = List.of();
}
