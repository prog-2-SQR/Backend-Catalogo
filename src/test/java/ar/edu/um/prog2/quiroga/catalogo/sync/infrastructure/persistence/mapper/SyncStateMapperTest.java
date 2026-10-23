package ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.persistence.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;

import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.SyncState;
import ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.persistence.entity.SyncStateEntity;
import org.junit.jupiter.api.Test;

class SyncStateMapperTest {

    private final SyncStateMapper syncStateMapper = new SyncStateMapper();

    @Test
    void toEntityAlwaysUsesTheSingleRow() {
        SyncState state = SyncState.builder().snapshotVersion(4L).appliedAt(Instant.parse("2026-10-23T12:00:00Z")).build();

        SyncStateEntity entity = syncStateMapper.toEntity(state);

        assertThat(entity.getId()).isEqualTo(SyncStateEntity.SINGLE_ROW_ID);
        assertThat(syncStateMapper.toDomain(entity)).usingRecursiveComparison().isEqualTo(state);
    }

    @Test
    void nullInputsReturnNull() {
        assertThat(syncStateMapper.toEntity(null)).isNull();
        assertThat(syncStateMapper.toDomain(null)).isNull();
    }
}
