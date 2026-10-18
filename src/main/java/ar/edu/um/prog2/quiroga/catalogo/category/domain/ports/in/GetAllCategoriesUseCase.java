package ar.edu.um.prog2.quiroga.catalogo.category.domain.ports.in;

import java.util.List;

import ar.edu.um.prog2.quiroga.catalogo.category.domain.model.Category;

public interface GetAllCategoriesUseCase {

    List<Category> getAllCategories();
}
