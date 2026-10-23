package ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.persistence.adapter;

import java.util.Optional;

import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.SyncState;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.out.SyncStateRepository;
import ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.persistence.entity.SyncStateEntity;
import ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.persistence.mapper.SyncStateMapper;
import ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.persistence.repository.JpaSyncStateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JpaSyncStateRepositoryAdapter implements SyncStateRepository {

    private final JpaSyncStateRepository jpaSyncStateRepository;
    private final SyncStateMapper syncStateMapper;

    @Override
    public Optional<SyncState> findSyncState() {
        return jpaSyncStateRepository.findById(SyncStateEntity.SINGLE_ROW_ID)
                .map(syncStateMapper::toDomain);
    }

    @Override
    public SyncState saveSyncState(SyncState syncState) {
        SyncStateEntity saved = jpaSyncStateRepository.save(syncStateMapper.toEntity(syncState));
        return syncStateMapper.toDomain(saved);
    }
}
