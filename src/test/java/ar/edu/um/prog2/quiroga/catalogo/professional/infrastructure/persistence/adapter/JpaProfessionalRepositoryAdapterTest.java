package ar.edu.um.prog2.quiroga.catalogo.professional.infrastructure.persistence.adapter;

import static ar.edu.um.prog2.quiroga.catalogo.professional.support.ProfessionalFixtures.anaTorres;
import static ar.edu.um.prog2.quiroga.catalogo.professional.support.ProfessionalFixtures.emilianoOrtiz;
import static org.assertj.core.api.Assertions.assertThat;

import ar.edu.um.prog2.quiroga.catalogo.TestcontainersConfiguration;
import ar.edu.um.prog2.quiroga.catalogo.professional.domain.model.Professional;
import ar.edu.um.prog2.quiroga.catalogo.professional.domain.model.ProfessionalPage;
import ar.edu.um.prog2.quiroga.catalogo.professional.domain.model.ProfessionalSearch;
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

    // Ana Torres (101, habilitada), Emiliano Ortiz (105, deshabilitado) y tres más de la categoría 101.
    private void storeFive() {
        adapter.upsertProfessional(anaTorres());
        adapter.upsertProfessional(emilianoOrtiz());
        adapter.upsertProfessional(professional(202L, "Bruno", "Alvarez"));
        adapter.upsertProfessional(professional(203L, "Carla", "Benitez"));
        adapter.upsertProfessional(professional(204L, "Dario", "100%_Real"));
    }

    private static Professional professional(Long id, String firstName, String lastName) {
        Professional professional = anaTorres();
        professional.setId(id);
        professional.setFirstName(firstName);
        professional.setLastName(lastName);
        return professional;
    }

    private ProfessionalPage search(ProfessionalSearch.ProfessionalSearchBuilder search) {
        return adapter.searchProfessionals(search.page(0).size(20).build());
    }

    @Test
    void searchWithoutFiltersReturnsEveryoneOrderedByLastNameAndFirstName() {
        storeFive();

        ProfessionalPage page = search(ProfessionalSearch.builder());

        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getContent()).extracting(Professional::getLastName)
                .containsExactly("100%_Real", "Alvarez", "Benitez", "Ortiz", "Torres");
    }

    @Test
    void searchFiltersByCategoryAndEnabledTogether() {
        storeFive();

        assertThat(search(ProfessionalSearch.builder().categoryId(105L)).getContent())
                .extracting(Professional::getId).containsExactly(224L);
        assertThat(search(ProfessionalSearch.builder().enabled(false)).getContent())
                .extracting(Professional::getId).containsExactly(224L);
        assertThat(search(ProfessionalSearch.builder().categoryId(101L).enabled(true)).getTotalElements()).isEqualTo(4);
        assertThat(search(ProfessionalSearch.builder().categoryId(105L).enabled(true)).getContent()).isEmpty();
    }

    @Test
    void searchByNameIgnoresCaseAndMatchesFirstNameLastNameOrBoth() {
        storeFive();

        assertThat(search(ProfessionalSearch.builder().name("ANA")).getContent())
                .extracting(Professional::getId).containsExactly(201L);
        assertThat(search(ProfessionalSearch.builder().name("torr")).getContent())
                .extracting(Professional::getId).containsExactly(201L);
        assertThat(search(ProfessionalSearch.builder().name("  ana tor  ")).getContent())
                .extracting(Professional::getId).containsExactly(201L);
    }

    @Test
    void likeWildcardsTypedByTheUserAreSearchedAsText() {
        storeFive();

        assertThat(search(ProfessionalSearch.builder().name("%")).getContent())
                .extracting(Professional::getId).containsExactly(204L);
        assertThat(search(ProfessionalSearch.builder().name("_")).getContent())
                .extracting(Professional::getId).containsExactly(204L);
    }

    @Test
    void searchIsPaginated() {
        storeFive();

        ProfessionalPage second = adapter.searchProfessionals(ProfessionalSearch.builder().page(1).size(2).build());

        assertThat(second.getContent()).extracting(Professional::getLastName).containsExactly("Benitez", "Ortiz");
        assertThat(second.getPage()).isEqualTo(1);
        assertThat(second.getSize()).isEqualTo(2);
        assertThat(second.getTotalElements()).isEqualTo(5);
        assertThat(second.totalPages()).isEqualTo(3);
    }
}
