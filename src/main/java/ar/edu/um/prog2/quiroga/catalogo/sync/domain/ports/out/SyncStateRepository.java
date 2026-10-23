package ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.out;

import java.util.Optional;

import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.SyncState;

public interface SyncStateRepository {

    Optional<SyncState> findSyncState();

    SyncState saveSyncState(SyncState syncState);
}
