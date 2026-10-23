package ar.edu.um.prog2.quiroga.catalogo.sync.application.usecases;

import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.SyncState;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.in.InitializeCatalogUseCase;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.in.SynchronizeSnapshotUseCase;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.out.SyncStateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InitializeCatalogUseCaseImpl implements InitializeCatalogUseCase {

    private final SyncStateRepository syncStateRepository;
    private final SynchronizeSnapshotUseCase synchronizeSnapshotUseCase;

    @Override
    public SyncState initializeCatalog() {
        return syncStateRepository.findSyncState()
                .orElseGet(synchronizeSnapshotUseCase::synchronizeSnapshot);
    }
}
