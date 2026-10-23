package ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.persistence.repository;

import ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.persistence.entity.SyncStateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaSyncStateRepository extends JpaRepository<SyncStateEntity, Short> {
}
