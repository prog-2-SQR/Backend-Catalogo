package ar.edu.um.prog2.quiroga.catalogo.sync.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;

import ar.edu.um.prog2.quiroga.catalogo.sync.application.exception.SyncException;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.CatalogVersions;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.SyncActivity;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.SyncHealth;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.SyncState;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.SyncStatus;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.out.CatalogChangeFeed;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.out.SyncActivityRepository;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.out.SyncStateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetSyncStatusUseCaseImplTest {

    private static final Instant APPLIED_AT = Instant.parse("2026-10-28T10:00:00Z");
    private static final Instant FAILED_AT = Instant.parse("2026-10-28T11:00:00Z");

    @Mock
    private SyncStateRepository syncStateRepository;

    @Mock
    private CatalogChangeFeed catalogChangeFeed;

    @Mock
    private SyncActivityRepository syncActivityRepository;

    @InjectMocks
    private GetSyncStatusUseCaseImpl getSyncStatusUseCase;

    @Test
    void combinesTheLocalVersionThePublishedOneAndTheLastAttempts() {
        when(syncStateRepository.findSyncState()).thenReturn(Optional.of(SyncState.builder().snapshotVersion(3L).appliedAt(APPLIED_AT).build()));
        when(catalogChangeFeed.findVersions()).thenReturn(Optional.of(CatalogVersions.builder().currentVersion(4L).oldestAvailableVersion(1L).build()));
        when(syncActivityRepository.findActivity()).thenReturn(SyncActivity.builder()
                .lastSuccessAt(APPLIED_AT).lastFailureAt(FAILED_AT).lastFailureMessage("Falta en Redis la versión 4").build());

        SyncStatus status = getSyncStatusUseCase.getSyncStatus();

        assertThat(status.getLocalVersion()).isEqualTo(3L);
        assertThat(status.getAppliedAt()).isEqualTo(APPLIED_AT);
        assertThat(status.getPublishedVersion()).isEqualTo(4L);
        assertThat(status.getLastFailureMessage()).isEqualTo("Falta en Redis la versión 4");
        assertThat(status.getHealth()).isEqualTo(SyncHealth.FAILING);
    }

    @Test
    void emptyDatabaseIsNotInitialized() {
        when(syncStateRepository.findSyncState()).thenReturn(Optional.empty());
        when(catalogChangeFeed.findVersions()).thenReturn(Optional.empty());
        when(syncActivityRepository.findActivity()).thenReturn(new SyncActivity());

        SyncStatus status = getSyncStatusUseCase.getSyncStatus();

        assertThat(status.getLocalVersion()).isNull();
        assertThat(status.getHealth()).isEqualTo(SyncHealth.NOT_INITIALIZED);
    }

    @Test
    void redisDownStillAnswersWithAnUnknownPublishedVersion() {
        when(syncStateRepository.findSyncState()).thenReturn(Optional.of(SyncState.builder().snapshotVersion(3L).build()));
        when(catalogChangeFeed.findVersions()).thenThrow(new SyncException("No se pudo leer el Redis de la cátedra"));
        when(syncActivityRepository.findActivity()).thenReturn(new SyncActivity());

        SyncStatus status = getSyncStatusUseCase.getSyncStatus();

        assertThat(status.getPublishedVersion()).isNull();
        assertThat(status.getHealth()).isEqualTo(SyncHealth.UNKNOWN);
    }
}
