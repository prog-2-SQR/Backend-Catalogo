package ar.edu.um.prog2.quiroga.catalogo.category.infrastructure.web.mapper;

import static ar.edu.um.prog2.quiroga.catalogo.category.support.CategoryFixtures.cardiologia;
import static org.assertj.core.api.Assertions.assertThat;

import ar.edu.um.prog2.quiroga.catalogo.category.infrastructure.web.dto.CategoryResponse;
import org.junit.jupiter.api.Test;

class CategoryDtoMapperTest {

    private final CategoryDtoMapper categoryDtoMapper = new CategoryDtoMapper();

    @Test
    void toResponseExposesOnlyThePublicFields() {
        CategoryResponse response = categoryDtoMapper.toResponse(cardiologia());

        assertThat(response.getId()).isEqualTo(101L);
        assertThat(response.getName()).isEqualTo("Cardiologia");
        assertThat(response.getDescription()).isEqualTo("Atencion cardiovascular");
        assertThat(response.getEnabled()).isTrue();
    }

    @Test
    void nullInputReturnsNull() {
        assertThat(categoryDtoMapper.toResponse(null)).isNull();
    }
}
