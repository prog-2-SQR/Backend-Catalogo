package ar.edu.um.prog2.quiroga.catalogo.category.infrastructure.persistence.adapter;

import ar.edu.um.prog2.quiroga.catalogo.category.domain.model.Category;
import ar.edu.um.prog2.quiroga.catalogo.category.domain.ports.out.CategoryRepository;
import ar.edu.um.prog2.quiroga.catalogo.category.infrastructure.persistence.entity.CategoryEntity;
import ar.edu.um.prog2.quiroga.catalogo.category.infrastructure.persistence.mapper.CategoryMapper;
import ar.edu.um.prog2.quiroga.catalogo.category.infrastructure.persistence.repository.JpaCategoryRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JpaCategoryRepositoryAdapter implements CategoryRepository {

    private final JpaCategoryRepository jpaCategoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public Optional<Category> findCategoryById(Long id) {
        return jpaCategoryRepository.findById(id)
                .map(categoryMapper::toDomain);
    }

    @Override
    public List<Category> findAllCategories() {
        return jpaCategoryRepository.findAll()
                .stream()
                .map(categoryMapper::toDomain)
                .toList();
    }

    @Override
    public Category upsertCategory(Category category) {
        CategoryEntity entity = categoryMapper.toEntity(category);
        CategoryEntity saved = jpaCategoryRepository.save(entity);
        return categoryMapper.toDomain(saved);
    }
}
