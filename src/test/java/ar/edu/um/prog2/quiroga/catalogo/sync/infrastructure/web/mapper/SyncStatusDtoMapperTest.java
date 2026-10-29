package ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.web.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;

import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.SyncStatus;
import ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.web.dto.SyncStatusResponse;
import org.junit.jupiter.api.Test;

class SyncStatusDtoMapperTest {

    private final SyncStatusDtoMapper mapper = new SyncStatusDtoMapper();

    @Test
    void copiesTheVersionsTheAttemptsAndTheHealth() {
        Instant appliedAt = Instant.parse("2026-10-28T10:00:00Z");
        Instant failedAt = Instant.parse("2026-10-28T11:00:00Z");

        SyncStatusResponse response = mapper.toResponse(SyncStatus.builder()
                .localVersion(3L).appliedAt(appliedAt).publishedVersion(3L)
                .lastSuccessAt(appliedAt).lastFailureAt(failedAt).lastFailureMessage("Falta en Redis la versión 4").build());

        assertThat(response.getHealth()).isEqualTo("FAILING");
        assertThat(response.getLocalVersion()).isEqualTo(3L);
        assertThat(response.getAppliedAt()).isEqualTo(appliedAt);
        assertThat(response.getPublishedVersion()).isEqualTo(3L);
        assertThat(response.getLastSuccessAt()).isEqualTo(appliedAt);
        assertThat(response.getLastFailureAt()).isEqualTo(failedAt);
        assertThat(response.getLastFailureMessage()).isEqualTo("Falta en Redis la versión 4");
    }

    @Test
    void nullStaysNull() {
        assertThat(mapper.toResponse(null)).isNull();
    }
}
