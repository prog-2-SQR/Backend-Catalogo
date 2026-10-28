package ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.persistence.adapter;

import java.time.Instant;

import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.CatalogUpdate;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.out.ProcessedEventRepository;
import ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.persistence.mapper.ProcessedEventMapper;
import ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.persistence.repository.JpaProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JpaProcessedEventRepositoryAdapter implements ProcessedEventRepository {

    private final JpaProcessedEventRepository jpaProcessedEventRepository;
    private final ProcessedEventMapper processedEventMapper;

    @Override
    public boolean existsByEventId(String eventId) {
        return jpaProcessedEventRepository.existsById(eventId);
    }

    @Override
    public void saveProcessedEvent(CatalogUpdate update) {
        jpaProcessedEventRepository.save(processedEventMapper.toEntity(update, Instant.now()));
    }
}
