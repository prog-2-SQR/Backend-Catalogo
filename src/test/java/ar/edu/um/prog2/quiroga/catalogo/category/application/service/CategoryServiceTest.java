package ar.edu.um.prog2.quiroga.catalogo.category.application.service;

import static ar.edu.um.prog2.quiroga.catalogo.category.support.CategoryFixtures.cardiologia;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import ar.edu.um.prog2.quiroga.catalogo.category.application.exception.CategoryException;
import ar.edu.um.prog2.quiroga.catalogo.category.domain.model.Category;
import ar.edu.um.prog2.quiroga.catalogo.category.domain.ports.in.FindCategoryByIdUseCase;
import ar.edu.um.prog2.quiroga.catalogo.category.domain.ports.in.GetAllCategoriesUseCase;
import ar.edu.um.prog2.quiroga.catalogo.category.domain.ports.in.UpsertCategoryUseCase;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private GetAllCategoriesUseCase getAllCategoriesUseCase;
    @Mock
    private FindCategoryByIdUseCase findCategoryByIdUseCase;
    @Mock
    private UpsertCategoryUseCase upsertCategoryUseCase;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void getAllCategoriesDelegatesToTheUseCase() {
        when(getAllCategoriesUseCase.getAllCategories()).thenReturn(List.of(cardiologia()));

        assertThat(categoryService.getAllCategories()).hasSize(1);
    }

    @Test
    void findCategoryByIdReturnsTheCategory() {
        when(findCategoryByIdUseCase.findCategoryById(101L)).thenReturn(Optional.of(cardiologia()));

        Category result = categoryService.findCategoryById(101L);

        assertThat(result.getName()).isEqualTo("Cardiologia");
    }

    @Test
    void findCategoryByIdThrowsCategoryExceptionWhenItDoesNotExist() {
        when(findCategoryByIdUseCase.findCategoryById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.findCategoryById(999L))
                .isInstanceOf(CategoryException.class)
                .hasMessageContaining("999");
    }

    @Test
    void upsertCategoryDelegatesToTheUseCase() {
        Category category = cardiologia();
        when(upsertCategoryUseCase.upsertCategory(category)).thenReturn(category);

        assertThat(categoryService.upsertCategory(category)).isSameAs(category);
    }

    @Test
    void categoryExceptionWithoutIdHasAGenericMessage() {
        assertThat(new CategoryException()).hasMessage("Categoría no encontrada");
    }
}
