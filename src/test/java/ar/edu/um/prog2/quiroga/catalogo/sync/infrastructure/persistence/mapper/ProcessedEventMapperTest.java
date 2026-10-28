package ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.persistence.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;

import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.CatalogUpdate;
import ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.persistence.entity.ProcessedEventEntity;
import org.junit.jupiter.api.Test;

class ProcessedEventMapperTest {

    private final ProcessedEventMapper mapper = new ProcessedEventMapper();

    @Test
    void copiesTheEventIdTheVersionAndTheProcessingTime() {
        Instant processedAt = Instant.parse("2026-10-28T12:00:00Z");

        ProcessedEventEntity entity = mapper.toEntity(
                CatalogUpdate.builder().eventId("catalog-event-7-4f3b").newVersion(7L).build(), processedAt);

        assertThat(entity.getEventId()).isEqualTo("catalog-event-7-4f3b");
        assertThat(entity.getNewVersion()).isEqualTo(7L);
        assertThat(entity.getProcessedAt()).isEqualTo(processedAt);
    }

    @Test
    void nullStaysNull() {
        assertThat(mapper.toEntity(null, Instant.now())).isNull();
    }
}
