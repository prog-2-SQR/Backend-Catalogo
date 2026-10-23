package ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.in;

import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.SyncState;

public interface SynchronizeSnapshotUseCase {

    SyncState synchronizeSnapshot();
}
