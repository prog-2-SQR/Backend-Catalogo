package ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.persistence.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;

import ar.edu.um.prog2.quiroga.catalogo.TestcontainersConfiguration;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.SyncState;
import ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.persistence.mapper.SyncStateMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestcontainersConfiguration.class, JpaSyncStateRepositoryAdapter.class, SyncStateMapper.class})
class JpaSyncStateRepositoryAdapterTest {

    @Autowired
    private JpaSyncStateRepositoryAdapter adapter;

    @Test
    void emptyDatabaseHasNoVersion() {
        assertThat(adapter.findSyncState()).isEmpty();
    }

    @Test
    void savingTwiceKeepsASingleRowWithTheLastVersion() {
        adapter.saveSyncState(SyncState.builder().snapshotVersion(1L).appliedAt(Instant.parse("2026-10-23T12:00:00Z")).build());
        adapter.saveSyncState(SyncState.builder().snapshotVersion(2L).appliedAt(Instant.parse("2026-10-23T13:00:00Z")).build());

        assertThat(adapter.findSyncState()).get()
                .extracting(SyncState::getSnapshotVersion).isEqualTo(2L);
    }
}
