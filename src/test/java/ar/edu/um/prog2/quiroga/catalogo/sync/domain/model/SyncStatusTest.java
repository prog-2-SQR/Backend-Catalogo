package ar.edu.um.prog2.quiroga.catalogo.sync.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;

import org.junit.jupiter.api.Test;

class SyncStatusTest {

    private static final Instant TEN = Instant.parse("2026-10-28T10:00:00Z");
    private static final Instant ELEVEN = Instant.parse("2026-10-28T11:00:00Z");

    @Test
    void withoutLocalVersionIsNotInitialized() {
        assertThat(SyncStatus.builder().publishedVersion(3L).build().getHealth()).isEqualTo(SyncHealth.NOT_INITIALIZED);
    }

    @Test
    void sameVersionAsPublishedIsUpToDate() {
        assertThat(SyncStatus.builder().localVersion(3L).publishedVersion(3L).build().getHealth()).isEqualTo(SyncHealth.UP_TO_DATE);
    }

    @Test
    void olderThanPublishedIsOutdated() {
        assertThat(SyncStatus.builder().localVersion(2L).publishedVersion(3L).build().getHealth()).isEqualTo(SyncHealth.OUTDATED);
    }

    @Test
    void unknownPublishedVersionIsUnknown() {
        assertThat(SyncStatus.builder().localVersion(3L).build().getHealth()).isEqualTo(SyncHealth.UNKNOWN);
    }

    @Test
    void aFailureAfterTheLastSuccessIsFailing() {
        SyncStatus status = SyncStatus.builder().localVersion(3L).publishedVersion(3L)
                .lastSuccessAt(TEN).lastFailureAt(ELEVEN).lastFailureMessage("No se pudo leer el Redis de la cátedra").build();

        assertThat(status.getHealth()).isEqualTo(SyncHealth.FAILING);
    }

    @Test
    void aFailureFollowedByASuccessIsNoLongerFailing() {
        SyncStatus status = SyncStatus.builder().localVersion(3L).publishedVersion(3L)
                .lastFailureAt(TEN).lastSuccessAt(ELEVEN).build();

        assertThat(status.getHealth()).isEqualTo(SyncHealth.UP_TO_DATE);
    }

    @Test
    void aFailureWithoutAnySuccessIsFailing() {
        assertThat(SyncStatus.builder().localVersion(3L).publishedVersion(3L).lastFailureAt(TEN).build().getHealth())
                .isEqualTo(SyncHealth.FAILING);
    }
}
