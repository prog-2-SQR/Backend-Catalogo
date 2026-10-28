package ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.persistence.mapper;

import java.time.Instant;

import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.CatalogUpdate;
import ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.persistence.entity.ProcessedEventEntity;
import org.springframework.stereotype.Component;

@Component
public class ProcessedEventMapper {

    public ProcessedEventEntity toEntity(CatalogUpdate update, Instant processedAt) {
        if (update == null) return null;
        return ProcessedEventEntity.builder()
                .eventId(update.getEventId())
                .newVersion(update.getNewVersion())
                .processedAt(processedAt)
                .build();
    }
}
