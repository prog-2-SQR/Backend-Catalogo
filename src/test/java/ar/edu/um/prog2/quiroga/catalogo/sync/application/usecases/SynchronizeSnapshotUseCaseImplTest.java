package ar.edu.um.prog2.quiroga.catalogo.sync.application.usecases;

import static ar.edu.um.prog2.quiroga.catalogo.sync.support.SyncFixtures.snapshot;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.CatalogSnapshot;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.SyncState;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.in.ApplySnapshotUseCase;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.out.CatalogSnapshotClient;
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

    @InjectMocks
    private SynchronizeSnapshotUseCaseImpl synchronizeSnapshotUseCase;

    @Test
    void fetchesTheSnapshotAndAppliesIt() {
        CatalogSnapshot snapshot = snapshot(3);
        SyncState applied = SyncState.builder().snapshotVersion(3L).build();
        when(catalogSnapshotClient.fetchSnapshot()).thenReturn(snapshot);
        when(applySnapshotUseCase.applySnapshot(snapshot)).thenReturn(applied);

        assertThat(synchronizeSnapshotUseCase.synchronizeSnapshot()).isSameAs(applied);
    }
}
