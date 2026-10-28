package ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.out;

import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.CatalogUpdate;

public interface ProcessedEventRepository {

    boolean existsByEventId(String eventId);

    void saveProcessedEvent(CatalogUpdate update);
}
