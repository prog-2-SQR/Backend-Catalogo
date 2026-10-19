package ar.edu.um.prog2.quiroga.catalogo.category.infrastructure.persistence.mapper;

import static ar.edu.um.prog2.quiroga.catalogo.category.support.CategoryFixtures.cardiologia;
import static org.assertj.core.api.Assertions.assertThat;

import ar.edu.um.prog2.quiroga.catalogo.category.domain.model.Category;
import ar.edu.um.prog2.quiroga.catalogo.category.infrastructure.persistence.entity.CategoryEntity;
import org.junit.jupiter.api.Test;

class CategoryMapperTest {

    private final CategoryMapper categoryMapper = new CategoryMapper();

    @Test
    void domainToEntityAndBackKeepsAllFields() {
        Category original = cardiologia();

        CategoryEntity entity = categoryMapper.toEntity(original);
        Category back = categoryMapper.toDomain(entity);

        assertThat(entity.getId()).isEqualTo(101L);
        assertThat(back).usingRecursiveComparison().isEqualTo(original);
    }

    @Test
    void nullInputsReturnNull() {
        assertThat(categoryMapper.toEntity(null)).isNull();
        assertThat(categoryMapper.toDomain(null)).isNull();
    }
}
