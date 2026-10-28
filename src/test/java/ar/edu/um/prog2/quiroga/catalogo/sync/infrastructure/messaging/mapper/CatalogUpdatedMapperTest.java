package ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.messaging.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;

import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.CatalogUpdate;
import ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.messaging.dto.CatalogUpdatedMessage;
import org.junit.jupiter.api.Test;

class CatalogUpdatedMapperTest {

    private final CatalogUpdatedMapper mapper = new CatalogUpdatedMapper();

    @Test
    void keepsTheEventIdTheVersionAndTheDate() {
        CatalogUpdate update = mapper.toDomain(CatalogUpdatedMessage.builder()
                .eventId("catalog-event-7-4f3b")
                .eventType("CatalogUpdated")
                .newVersion(7L)
                .occurredAt(Instant.parse("2026-09-10T13:44:59Z"))
                .source("MANUAL")
                .schemaVersion(1)
                .build());

        assertThat(update.getEventId()).isEqualTo("catalog-event-7-4f3b");
        assertThat(update.getNewVersion()).isEqualTo(7L);
        assertThat(update.getOccurredAt()).isEqualTo(Instant.parse("2026-09-10T13:44:59Z"));
    }

    @Test
    void nullStaysNull() {
        assertThat(mapper.toDomain(null)).isNull();
    }
}
