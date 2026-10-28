package ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.messaging.listener;

import java.util.Optional;

import ar.edu.um.prog2.quiroga.catalogo.sync.application.service.SyncService;
import ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.messaging.dto.CatalogUpdatedMessage;
import ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.messaging.mapper.CatalogUpdatedMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

/**
 * Escucha los avisos de versión nueva del catálogo (anexo §15.3) y dispara la sincronización incremental.
 * El mensaje llega como texto y se lee con el JsonMapper de la app, como en Redis: no depende de headers de tipo
 * del productor. Un mensaje que no se puede usar se descarta con un log, porque reintentarlo no lo arregla.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CatalogUpdatedListener {

    static final String LISTENER_ID = "catalogUpdatedListener";
    private static final String EVENT_TYPE = "CatalogUpdated";
    private static final int MAX_LOGGED_LENGTH = 200;

    private final SyncService syncService;
    private final JsonMapper jsonMapper;
    private final CatalogUpdatedMapper catalogUpdatedMapper;

    // idIsGroup = false: el consumer group es el de la integración (spring.kafka.consumer.group-id), no el id.
    @KafkaListener(id = LISTENER_ID, idIsGroup = false, topics = "${catedra.catalog-topic}")
    public void onCatalogUpdated(String payload) {
        Optional<CatalogUpdatedMessage> message = parse(payload).filter(CatalogUpdatedListener::isProcessable);
        if (message.isEmpty()) {
            log.warn("Mensaje descartado del topic de catálogo: {}", abbreviate(payload));
            return;
        }
        syncService.handleCatalogUpdate(catalogUpdatedMapper.toDomain(message.get()));
    }

    private Optional<CatalogUpdatedMessage> parse(String payload) {
        try {
            return Optional.ofNullable(jsonMapper.readValue(payload, CatalogUpdatedMessage.class));
        } catch (JacksonException e) {
            return Optional.empty();
        }
    }

    private static boolean isProcessable(CatalogUpdatedMessage message) {
        return EVENT_TYPE.equals(message.getEventType())
                && message.getEventId() != null && !message.getEventId().isBlank()
                && message.getNewVersion() != null;
    }

    private static String abbreviate(String payload) {
        if (payload == null || payload.length() <= MAX_LOGGED_LENGTH) {
            return payload;
        }
        return payload.substring(0, MAX_LOGGED_LENGTH) + "…";
    }
}
