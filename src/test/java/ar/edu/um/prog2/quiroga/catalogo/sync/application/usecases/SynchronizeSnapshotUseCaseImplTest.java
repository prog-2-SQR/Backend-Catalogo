package ar.edu.um.prog2.quiroga.catalogo.sync.application.usecases;

import static ar.edu.um.prog2.quiroga.catalogo.sync.support.SyncFixtures.snapshot;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ar.edu.um.prog2.quiroga.catalogo.sync.application.exception.SyncException;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.CatalogSnapshot;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.SyncState;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.in.ApplySnapshotUseCase;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.out.CatalogSnapshotClient;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.out.SyncActivityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SynchronizeSnapshotUseCaseImplTest {

    @Mock
    private CatalogSnapshotClient catalogSnapshotClient;

    @Mock
    private ApplySnapshotUseCase applySnapshotUseCase;

    @Mock
    private SyncActivityRepository syncActivityRepository;

    @InjectMocks
    private SynchronizeSnapshotUseCaseImpl synchronizeSnapshotUseCase;

    @Test
    void fetchesTheSnapshotAndAppliesIt() {
        CatalogSnapshot snapshot = snapshot(3);
        SyncState applied = SyncState.builder().snapshotVersion(3L).build();
        when(catalogSnapshotClient.fetchSnapshot()).thenReturn(snapshot);
        when(applySnapshotUseCase.applySnapshot(snapshot)).thenReturn(applied);

        assertThat(synchronizeSnapshotUseCase.synchronizeSnapshot()).isSameAs(applied);
        verify(syncActivityRepository).recordSuccess();
    }

    @Test
    void aFailedSnapshotIsRecordedWithItsMessageAndRethrown() {
        when(catalogSnapshotClient.fetchSnapshot()).thenThrow(new SyncException("No se pudo obtener el snapshot de la cátedra"));

        assertThatThrownBy(() -> synchronizeSnapshotUseCase.synchronizeSnapshot()).isInstanceOf(SyncException.class);

        verify(syncActivityRepository).recordFailure("No se pudo obtener el snapshot de la cátedra");
        verify(syncActivityRepository, never()).recordSuccess();
    }
}
