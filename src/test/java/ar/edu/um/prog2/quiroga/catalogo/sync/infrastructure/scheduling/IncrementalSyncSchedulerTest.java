package ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.scheduling;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ar.edu.um.prog2.quiroga.catalogo.sync.application.exception.SyncException;
import ar.edu.um.prog2.quiroga.catalogo.sync.application.service.SyncService;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.SyncState;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IncrementalSyncSchedulerTest {

    @Mock
    private SyncService syncService;

    @InjectMocks
    private IncrementalSyncScheduler scheduler;

    @Test
    void eachCheckSynchronizesTheChanges() {
        when(syncService.synchronizeChanges()).thenReturn(SyncState.builder().snapshotVersion(3L).build());

        scheduler.synchronize();

        verify(syncService).synchronizeChanges();
    }

    @Test
    void aFailedCheckDoesNotStopTheNextOnes() {
        when(syncService.synchronizeChanges()).thenThrow(new SyncException("La cátedra no responde"));

        assertThatCode(() -> scheduler.synchronize()).doesNotThrowAnyException();
    }
}
