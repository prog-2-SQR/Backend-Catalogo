package ar.edu.um.prog2.quiroga.catalogo.sync.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.SyncState;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.in.InitializeCatalogUseCase;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.in.SynchronizeSnapshotUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SyncServiceTest {

    private static final SyncState STATE = SyncState.builder().snapshotVersion(2L).build();

    @Mock
    private SynchronizeSnapshotUseCase synchronizeSnapshotUseCase;

    @Mock
    private InitializeCatalogUseCase initializeCatalogUseCase;

    @InjectMocks
    private SyncService syncService;

    @Test
    void synchronizeSnapshotDelegatesToItsUseCase() {
        when(synchronizeSnapshotUseCase.synchronizeSnapshot()).thenReturn(STATE);

        assertThat(syncService.synchronizeSnapshot()).isSameAs(STATE);
    }

    @Test
    void initializeCatalogDelegatesToItsUseCase() {
        when(initializeCatalogUseCase.initializeCatalog()).thenReturn(STATE);

        assertThat(syncService.initializeCatalog()).isSameAs(STATE);
    }
}
