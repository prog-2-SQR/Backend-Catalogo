package ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.persistence.mapper;

import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.SyncState;
import ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.persistence.entity.SyncStateEntity;
import org.springframework.stereotype.Component;

@Component
public class SyncStateMapper {

    public SyncStateEntity toEntity(SyncState domain) {
        if (domain == null) return null;
        return SyncStateEntity.builder()
                .id(SyncStateEntity.SINGLE_ROW_ID)
                .snapshotVersion(domain.getSnapshotVersion())
                .appliedAt(domain.getAppliedAt())
                .build();
    }

    public SyncState toDomain(SyncStateEntity entity) {
        if (entity == null) return null;
        return SyncState.builder()
                .snapshotVersion(entity.getSnapshotVersion())
                .appliedAt(entity.getAppliedAt())
                .build();
    }
}
