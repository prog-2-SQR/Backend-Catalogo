package ar.edu.um.prog2.quiroga.catalogo.sync.application.service;

import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.CatalogUpdate;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.SyncState;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.in.HandleCatalogUpdateUseCase;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.in.InitializeCatalogUseCase;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.in.SynchronizeChangesUseCase;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.in.SynchronizeSnapshotUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SyncService {

    private final SynchronizeSnapshotUseCase synchronizeSnapshotUseCase;
    private final InitializeCatalogUseCase initializeCatalogUseCase;
    private final SynchronizeChangesUseCase synchronizeChangesUseCase;
    private final HandleCatalogUpdateUseCase handleCatalogUpdateUseCase;

    public SyncState synchronizeSnapshot() {
        return synchronizeSnapshotUseCase.synchronizeSnapshot();
    }

    public SyncState initializeCatalog() {
        return initializeCatalogUseCase.initializeCatalog();
    }

    public SyncState synchronizeChanges() {
        return synchronizeChangesUseCase.synchronizeChanges();
    }

    public void handleCatalogUpdate(CatalogUpdate update) {
        handleCatalogUpdateUseCase.handleCatalogUpdate(update);
    }
}
