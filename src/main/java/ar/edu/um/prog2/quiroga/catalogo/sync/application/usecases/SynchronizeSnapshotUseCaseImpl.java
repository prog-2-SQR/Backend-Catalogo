package ar.edu.um.prog2.quiroga.catalogo.sync.application.usecases;

import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.SyncState;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.in.ApplySnapshotUseCase;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.in.SynchronizeSnapshotUseCase;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.out.CatalogSnapshotClient;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.out.SyncActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Pide el snapshot y lo aplica. La llamada HTTP queda fuera de la transacción (no se tiene una conexión
 * a la base abierta mientras se espera a la cátedra); la transacción la abre ApplySnapshotUseCase.
 * El resultado queda registrado para el estado de la sincronización.
 */
@Component
@RequiredArgsConstructor
public class SynchronizeSnapshotUseCaseImpl implements SynchronizeSnapshotUseCase {

    private final CatalogSnapshotClient catalogSnapshotClient;
    private final ApplySnapshotUseCase applySnapshotUseCase;
    private final SyncActivityRepository syncActivityRepository;

    @Override
    public SyncState synchronizeSnapshot() {
        return SyncActivityRecording.recorded(syncActivityRepository,
                () -> applySnapshotUseCase.applySnapshot(catalogSnapshotClient.fetchSnapshot()));
    }
}
