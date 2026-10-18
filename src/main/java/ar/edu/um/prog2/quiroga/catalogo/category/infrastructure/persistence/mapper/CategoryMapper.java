package ar.edu.um.prog2.quiroga.catalogo.category.infrastructure.persistence.mapper;

import ar.edu.um.prog2.quiroga.catalogo.category.domain.model.Category;
import ar.edu.um.prog2.quiroga.catalogo.category.infrastructure.persistence.entity.CategoryEntity;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
    public CategoryEntity toEntity(Category domain) {
        if (domain == null) return null;
        return CategoryEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .description(domain.getDescription())
                .enabled(domain.getEnabled())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    public Category toDomain(CategoryEntity entity) {
        if (entity == null) return null;
        return Category.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .enabled(entity.getEnabled())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}