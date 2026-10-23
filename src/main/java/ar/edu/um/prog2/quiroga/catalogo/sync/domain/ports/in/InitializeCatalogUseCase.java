package ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.in;

import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.SyncState;

public interface InitializeCatalogUseCase {

    /** Si la copia local está vacía (sin versión), la llena con el snapshot; si no, la deja como está. */
    SyncState initializeCatalog();
}
