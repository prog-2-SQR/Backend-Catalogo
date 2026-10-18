package ar.edu.um.prog2.quiroga.catalogo.category.domain.ports.in;

import java.util.Optional;

import ar.edu.um.prog2.quiroga.catalogo.category.domain.model.Category;

public interface FindCategoryByIdUseCase {

    Optional<Category> findCategoryById(Long id);
}
