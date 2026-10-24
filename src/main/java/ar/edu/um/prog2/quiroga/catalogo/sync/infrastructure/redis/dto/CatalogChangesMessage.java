package ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.redis.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** JSON de catedra:sync:changes:{version} (anexo §14.5). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CatalogChangesMessage {

    private Long version;
    private ChangedIds changes;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ChangedIds {

        private List<Long> professionalCategories;
        private List<Long> professionals;
        private List<Long> weeklySchedules;
    }
}
