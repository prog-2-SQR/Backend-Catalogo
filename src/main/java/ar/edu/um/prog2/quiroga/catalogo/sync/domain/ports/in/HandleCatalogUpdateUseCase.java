package ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.in;

import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.CatalogUpdate;

public interface HandleCatalogUpdateUseCase {

    /**
     * Sincroniza los cambios publicados a partir de un aviso de versión nueva.
     * Un aviso que ya se procesó (mismo eventId) no tiene efecto.
     */
    void handleCatalogUpdate(CatalogUpdate update);
}
