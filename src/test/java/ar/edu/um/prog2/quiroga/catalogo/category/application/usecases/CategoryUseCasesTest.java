package ar.edu.um.prog2.quiroga.catalogo.category.application.usecases;

import static ar.edu.um.prog2.quiroga.catalogo.category.support.CategoryFixtures.cardiologia;
import static ar.edu.um.prog2.quiroga.catalogo.category.support.CategoryFixtures.pediatria;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ar.edu.um.prog2.quiroga.catalogo.category.domain.model.Category;
import ar.edu.um.prog2.quiroga.catalogo.category.domain.ports.out.CategoryRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoryUseCasesTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Test
    void getAllCategoriesReturnsWhatTheRepositoryHas() {
        when(categoryRepository.findAllCategories()).thenReturn(List.of(cardiologia(), pediatria()));

        List<Category> result = new GetAllCategoriesUseCaseImpl(categoryRepository).getAllCategories();

        assertThat(result).extracting(Category::getId).containsExactly(101L, 102L);
    }

    @Test
    void findCategoryByIdReturnsTheCategoryWhenItExists() {
        when(categoryRepository.findCategoryById(101L)).thenReturn(Optional.of(cardiologia()));

        Optional<Category> result = new FindCategoryByIdUseCaseImpl(categoryRepository).findCategoryById(101L);

        assertThat(result).get().extracting(Category::getName).isEqualTo("Cardiologia");
    }

    @Test
    void findCategoryByIdReturnsEmptyWhenItDoesNotExist() {
        when(categoryRepository.findCategoryById(999L)).thenReturn(Optional.empty());

        Optional<Category> result = new FindCategoryByIdUseCaseImpl(categoryRepository).findCategoryById(999L);

        assertThat(result).isEmpty();
    }

    @Test
    void upsertCategoryDelegatesToTheRepository() {
        Category category = cardiologia();
        when(categoryRepository.upsertCategory(category)).thenReturn(category);

        Category result = new UpsertCategoryUseCaseImpl(categoryRepository).upsertCategory(category);

        assertThat(result).isSameAs(category);
        verify(categoryRepository).upsertCategory(category);
    }
}
