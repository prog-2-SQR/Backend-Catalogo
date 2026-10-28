package ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.persistence.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import ar.edu.um.prog2.quiroga.catalogo.TestcontainersConfiguration;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.CatalogUpdate;
import ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.persistence.mapper.ProcessedEventMapper;
import ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.persistence.repository.JpaProcessedEventRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestcontainersConfiguration.class, JpaProcessedEventRepositoryAdapter.class, ProcessedEventMapper.class})
class JpaProcessedEventRepositoryAdapterTest {

    @Autowired
    private JpaProcessedEventRepositoryAdapter adapter;

    @Autowired
    private JpaProcessedEventRepository jpaProcessedEventRepository;

    @Test
    void unknownEventIsNotProcessed() {
        assertThat(adapter.existsByEventId("catalog-event-7-4f3b")).isFalse();
    }

    @Test
    void savedEventIsRecognizedByItsId() {
        adapter.saveProcessedEvent(CatalogUpdate.builder().eventId("catalog-event-7-4f3b").newVersion(7L).build());

        assertThat(adapter.existsByEventId("catalog-event-7-4f3b")).isTrue();
        assertThat(adapter.existsByEventId("catalog-event-8-9a1c")).isFalse();
        assertThat(jpaProcessedEventRepository.findById("catalog-event-7-4f3b")).get()
                .satisfies(entity -> {
                    assertThat(entity.getNewVersion()).isEqualTo(7L);
                    assertThat(entity.getProcessedAt()).isNotNull();
                });
    }
}
