package ar.edu.um.prog2.quiroga.catalogo.category.domain.ports.out;

import java.util.List;
import java.util.Optional;

import ar.edu.um.prog2.quiroga.catalogo.category.domain.model.Category;

public interface CategoryRepository {

    Optional<Category> findCategoryById(Long id);

    List<Category> findAllCategories();

    Category upsertCategory(Category category);
}
