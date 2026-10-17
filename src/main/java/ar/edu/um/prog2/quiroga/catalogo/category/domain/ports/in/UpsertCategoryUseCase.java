package ar.edu.um.prog2.quiroga.catalogo.category.domain.ports.in;

import ar.edu.um.prog2.quiroga.catalogo.category.domain.model.Category;

public interface UpsertCategoryUseCase {

    Category upsertCategory(Category category);
}
