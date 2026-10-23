package ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.out;

import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.CatalogSnapshot;

/** Obtiene el snapshot completo del catálogo desde la cátedra. */
public interface CatalogSnapshotClient {

    CatalogSnapshot fetchSnapshot();
}
