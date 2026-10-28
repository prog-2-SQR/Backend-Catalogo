package ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.persistence.repository;

import ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.persistence.entity.ProcessedEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaProcessedEventRepository extends JpaRepository<ProcessedEventEntity, String> {
}
