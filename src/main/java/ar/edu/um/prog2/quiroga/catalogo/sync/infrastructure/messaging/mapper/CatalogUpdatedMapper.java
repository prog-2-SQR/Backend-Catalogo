package ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.messaging.mapper;

import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.CatalogUpdate;
import ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.messaging.dto.CatalogUpdatedMessage;
import org.springframework.stereotype.Component;

@Component
public class CatalogUpdatedMapper {

    public CatalogUpdate toDomain(CatalogUpdatedMessage message) {
        if (message == null) return null;
        return CatalogUpdate.builder()
                .eventId(message.getEventId())
                .newVersion(message.getNewVersion())
                .occurredAt(message.getOccurredAt())
                .build();
    }
}
