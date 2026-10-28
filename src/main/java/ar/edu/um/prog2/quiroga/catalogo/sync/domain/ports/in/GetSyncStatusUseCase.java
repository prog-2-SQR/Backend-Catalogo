package ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.in;

import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.SyncStatus;

public interface GetSyncStatusUseCase {

    /** Versión local, versión publicada y resultado de los últimos intentos de sincronización. */
    SyncStatus getSyncStatus();
}
