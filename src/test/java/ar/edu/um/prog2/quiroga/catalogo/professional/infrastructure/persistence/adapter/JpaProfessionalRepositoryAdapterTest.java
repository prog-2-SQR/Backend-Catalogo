package ar.edu.um.prog2.quiroga.catalogo.professional.infrastructure.persistence.adapter;

import static ar.edu.um.prog2.quiroga.catalogo.professional.support.ProfessionalFixtures.anaTorres;
import static ar.edu.um.prog2.quiroga.catalogo.professional.support.ProfessionalFixtures.emilianoOrtiz;
import static org.assertj.core.api.Assertions.assertThat;

import ar.edu.um.prog2.quiroga.catalogo.TestcontainersConfiguration;
import ar.edu.um.prog2.quiroga.catalogo.professional.domain.model.Professional;
import ar.edu.um.prog2.quiroga.catalogo.professional.infrastructure.persistence.mapper.ProfessionalMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

/** Adapter contra un PostgreSQL real (Testcontainers) con la tabla creada por la migración de Liquibase. */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestcontainersConfiguration.class, JpaProfessionalRepositoryAdapter.class, ProfessionalMapper.class})
class JpaProfessionalRepositoryAdapterTest {

    @Autowired
    private JpaProfessionalRepositoryAdapter adapter;

    @Test
    void upsertInsertsANewProfessionalKeepingTheIdFromTheCatedra() {
        adapter.upsertProfessional(anaTorres());

        assertThat(adapter.findProfessionalById(201L))
                .get()
                .usingRecursiveComparison()
                .isEqualTo(anaTorres());
    }

    @Test
    void upsertUpdatesAnExistingProfessionalInsteadOfDuplicatingIt() {
        adapter.upsertProfessional(anaTorres());
        Professional changed = anaTorres();
        changed.setCategoryId(102L);
        changed.setEnabled(false);

        adapter.upsertProfessional(changed);

        assertThat(adapter.findAllProfessionals()).hasSize(1);
        assertThat(adapter.findProfessionalById(201L)).get()
                .extracting(Professional::getCategoryId, Professional::getEnabled)
                .containsExactly(102L, false);
    }

    @Test
    void storesAProfessionalWhoseCategoryIsNotStoredYet() {
        // Sin clave foránea (ADR 0003): la sincronización no depende del orden de llegada.
        adapter.upsertProfessional(emilianoOrtiz());

        assertThat(adapter.findProfessionalById(224L)).isPresent();
    }

    @Test
    void findProfessionalByIdReturnsEmptyWhenItDoesNotExist() {
        assertThat(adapter.findProfessionalById(999L)).isEmpty();
    }
}
