package ar.edu.um.prog2.quiroga.catalogo.sync.application.usecases;

import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.CatalogUpdate;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.SyncState;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.in.HandleCatalogUpdateUseCase;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.in.SynchronizeChangesUseCase;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.out.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Kafka entrega al menos una vez (anexo §15.1): un aviso repetido se reconoce por su eventId y no se vuelve a
 * procesar (anexo §18.1). El eventId se guarda recién después de sincronizar; si el proceso se corta en el medio,
 * el aviso llega de nuevo y la sincronización no repite nada porque compara versiones.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HandleCatalogUpdateUseCaseImpl implements HandleCatalogUpdateUseCase {

    private final ProcessedEventRepository processedEventRepository;
    private final SynchronizeChangesUseCase synchronizeChangesUseCase;

    @Override
    public void handleCatalogUpdate(CatalogUpdate update) {
        if (processedEventRepository.existsByEventId(update.getEventId())) {
            log.info("Aviso {} ya procesado: se ignora", update.getEventId());
            return;
        }
        SyncState state = synchronizeChangesUseCase.synchronizeChanges();
        processedEventRepository.saveProcessedEvent(update);
        log.info("Aviso {} de la versión {} procesado: catálogo local en la versión {}",
                update.getEventId(), update.getNewVersion(), state.getSnapshotVersion());
    }
}
