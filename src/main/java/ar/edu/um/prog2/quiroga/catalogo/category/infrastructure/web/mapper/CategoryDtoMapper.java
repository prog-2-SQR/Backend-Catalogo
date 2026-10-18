package ar.edu.um.prog2.quiroga.catalogo.category.infrastructure.web.mapper;

import ar.edu.um.prog2.quiroga.catalogo.category.domain.model.Category;
import ar.edu.um.prog2.quiroga.catalogo.category.infrastructure.web.dto.CategoryResponse;
import org.springframework.stereotype.Component;

@Component
public class CategoryDtoMapper {

    public CategoryResponse toResponse(Category domain) {
        if (domain == null) return null;
        return CategoryResponse.builder()
                .id(domain.getId())
                .name(domain.getName())
                .description(domain.getDescription())
                .enabled(domain.getEnabled())
                .build();
    }
}
