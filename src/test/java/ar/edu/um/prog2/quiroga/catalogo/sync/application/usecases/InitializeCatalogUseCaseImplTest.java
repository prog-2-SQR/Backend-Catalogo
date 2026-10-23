package ar.edu.um.prog2.quiroga.catalogo.sync.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.SyncState;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.in.SynchronizeSnapshotUseCase;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.out.SyncStateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InitializeCatalogUseCaseImplTest {

    @Mock
    private SyncStateRepository syncStateRepository;

    @Mock
    private SynchronizeSnapshotUseCase synchronizeSnapshotUseCase;

    @InjectMocks
    private InitializeCatalogUseCaseImpl initializeCatalogUseCase;

    @Test
    void emptyCatalogIsLoadedFromTheSnapshot() {
        SyncState applied = SyncState.builder().snapshotVersion(1L).build();
        when(syncStateRepository.findSyncState()).thenReturn(Optional.empty());
        when(synchronizeSnapshotUseCase.synchronizeSnapshot()).thenReturn(applied);

        assertThat(initializeCatalogUseCase.initializeCatalog()).isSameAs(applied);
    }

    @Test
    void catalogWithAVersionIsLeftAsItIs() {
        SyncState current = SyncState.builder().snapshotVersion(5L).build();
        when(syncStateRepository.findSyncState()).thenReturn(Optional.of(current));

        assertThat(initializeCatalogUseCase.initializeCatalog()).isSameAs(current);
        verify(synchronizeSnapshotUseCase, never()).synchronizeSnapshot();
    }
}
