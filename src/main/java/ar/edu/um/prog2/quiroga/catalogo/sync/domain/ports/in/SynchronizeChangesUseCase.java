package ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.in;

import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.SyncState;

public interface SynchronizeChangesUseCase {

    /**
     * Lleva la copia local a la última versión publicada aplicando los cambios incrementales en orden.
     * Si no se puede continuar de forma segura desde la versión local, hace una sincronización completa.
     */
    SyncState synchronizeChanges();
}
