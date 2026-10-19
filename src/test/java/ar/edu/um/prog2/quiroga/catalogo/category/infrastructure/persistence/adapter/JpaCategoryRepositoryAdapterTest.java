package ar.edu.um.prog2.quiroga.catalogo.category.infrastructure.persistence.adapter;

import static ar.edu.um.prog2.quiroga.catalogo.category.support.CategoryFixtures.cardiologia;
import static ar.edu.um.prog2.quiroga.catalogo.category.support.CategoryFixtures.pediatria;
import static org.assertj.core.api.Assertions.assertThat;

import ar.edu.um.prog2.quiroga.catalogo.TestcontainersConfiguration;
import ar.edu.um.prog2.quiroga.catalogo.category.domain.model.Category;
import ar.edu.um.prog2.quiroga.catalogo.category.infrastructure.persistence.mapper.CategoryMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

/** Adapter contra un PostgreSQL real (Testcontainers) con la tabla creada por la migración de Liquibase. */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestcontainersConfiguration.class, JpaCategoryRepositoryAdapter.class, CategoryMapper.class})
class JpaCategoryRepositoryAdapterTest {

    @Autowired
    private JpaCategoryRepositoryAdapter adapter;

    @Test
    void upsertInsertsANewCategoryKeepingTheIdFromTheCatedra() {
        adapter.upsertCategory(cardiologia());

        assertThat(adapter.findCategoryById(101L))
                .get()
                .usingRecursiveComparison()
                .isEqualTo(cardiologia());
    }

    @Test
    void upsertUpdatesAnExistingCategoryInsteadOfDuplicatingIt() {
        adapter.upsertCategory(cardiologia());
        Category changed = cardiologia();
        changed.setName("Cardiologia infantil");
        changed.setEnabled(false);

        Category result = adapter.upsertCategory(changed);

        assertThat(result.getName()).isEqualTo("Cardiologia infantil");
        assertThat(adapter.findAllCategories()).hasSize(1);
        assertThat(adapter.findCategoryById(101L)).get().extracting(Category::getEnabled).isEqualTo(false);
    }

    @Test
    void findAllCategoriesReturnsEveryStoredCategory() {
        adapter.upsertCategory(cardiologia());
        adapter.upsertCategory(pediatria());

        assertThat(adapter.findAllCategories()).extracting(Category::getId).containsExactlyInAnyOrder(101L, 102L);
    }

    @Test
    void findCategoryByIdReturnsEmptyWhenItDoesNotExist() {
        assertThat(adapter.findCategoryById(999L)).isEmpty();
    }
}
