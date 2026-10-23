package ar.edu.um.prog2.quiroga.catalogo.sync.application.usecases;

import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.SyncState;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.in.ApplySnapshotUseCase;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.in.SynchronizeSnapshotUseCase;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.out.CatalogSnapshotClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Pide el snapshot y lo aplica. La llamada HTTP queda fuera de la transacción (no se tiene una conexión
 * a la base abierta mientras se espera a la cátedra); la transacción la abre ApplySnapshotUseCase.
 */
@Component
@RequiredArgsConstructor
public class SynchronizeSnapshotUseCaseImpl implements SynchronizeSnapshotUseCase {

    private final CatalogSnapshotClient catalogSnapshotClient;
    private final ApplySnapshotUseCase applySnapshotUseCase;

    @Override
    public SyncState synchronizeSnapshot() {
        return applySnapshotUseCase.applySnapshot(catalogSnapshotClient.fetchSnapshot());
    }
}
