package ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.redis.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.CatalogChanges;
import ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.redis.dto.CatalogChangesMessage;
import org.junit.jupiter.api.Test;

class CatalogChangesMapperTest {

    private final CatalogChangesMapper catalogChangesMapper = new CatalogChangesMapper();

    @Test
    void toDomainCopiesTheVersionAndTheChangedIds() {
        CatalogChangesMessage message = CatalogChangesMessage.builder()
                .version(7L)
                .changes(CatalogChangesMessage.ChangedIds.builder()
                        .professionalCategories(List.of(101L)).professionals(List.of(201L, 202L)).weeklySchedules(List.of())
                        .build())
                .build();

        CatalogChanges changes = catalogChangesMapper.toDomain(message);

        assertThat(changes.getVersion()).isEqualTo(7L);
        assertThat(changes.getCategoryIds()).containsExactly(101L);
        assertThat(changes.getProfessionalIds()).containsExactly(201L, 202L);
        assertThat(changes.getWeeklyScheduleIds()).isEmpty();
    }

    @Test
    void missingListsBecomeEmpty() {
        CatalogChanges withoutChanges = catalogChangesMapper.toDomain(CatalogChangesMessage.builder().version(2L).build());
        CatalogChanges withEmptyChanges = catalogChangesMapper.toDomain(CatalogChangesMessage.builder()
                .version(3L).changes(new CatalogChangesMessage.ChangedIds()).build());

        assertThat(withoutChanges.getProfessionalIds()).isEmpty();
        assertThat(withEmptyChanges.getCategoryIds()).isEmpty();
        assertThat(withEmptyChanges.getWeeklyScheduleIds()).isEmpty();
    }

    @Test
    void nullInputReturnsNull() {
        assertThat(catalogChangesMapper.toDomain(null)).isNull();
    }
}
