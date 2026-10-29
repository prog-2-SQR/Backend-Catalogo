package ar.edu.um.prog2.quiroga.catalogo.sync.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.CatalogUpdate;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.SyncState;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.SyncStatus;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.in.GetSyncStatusUseCase;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.in.HandleCatalogUpdateUseCase;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.in.InitializeCatalogUseCase;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.in.SynchronizeChangesUseCase;
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

    @Mock
    private SynchronizeChangesUseCase synchronizeChangesUseCase;

    @Mock
    private HandleCatalogUpdateUseCase handleCatalogUpdateUseCase;

    @Mock
    private GetSyncStatusUseCase getSyncStatusUseCase;

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

    @Test
    void synchronizeChangesDelegatesToItsUseCase() {
        when(synchronizeChangesUseCase.synchronizeChanges()).thenReturn(STATE);

        assertThat(syncService.synchronizeChanges()).isSameAs(STATE);
    }

    @Test
    void handleCatalogUpdateDelegatesToItsUseCase() {
        CatalogUpdate update = CatalogUpdate.builder().eventId("catalog-event-7-4f3b").newVersion(7L).build();

        syncService.handleCatalogUpdate(update);

        verify(handleCatalogUpdateUseCase).handleCatalogUpdate(update);
    }

    @Test
    void getSyncStatusDelegatesToItsUseCase() {
        SyncStatus status = SyncStatus.builder().localVersion(2L).build();
        when(getSyncStatusUseCase.getSyncStatus()).thenReturn(status);

        assertThat(syncService.getSyncStatus()).isSameAs(status);
    }
}
