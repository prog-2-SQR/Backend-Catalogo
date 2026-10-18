package ar.edu.um.prog2.quiroga.catalogo.category.application.service;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import ar.edu.um.prog2.quiroga.catalogo.category.application.exception.CategoryException;
import ar.edu.um.prog2.quiroga.catalogo.category.domain.model.Category;
import ar.edu.um.prog2.quiroga.catalogo.category.domain.ports.in.FindCategoryByIdUseCase;
import ar.edu.um.prog2.quiroga.catalogo.category.domain.ports.in.GetAllCategoriesUseCase;
import ar.edu.um.prog2.quiroga.catalogo.category.domain.ports.in.UpsertCategoryUseCase;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final GetAllCategoriesUseCase getAllCategoriesUseCase;
    private final FindCategoryByIdUseCase findCategoryByIdUseCase;
    private final UpsertCategoryUseCase upsertCategoryUseCase;

    public List<Category> getAllCategories() {
        return getAllCategoriesUseCase.getAllCategories();
    }

    public Category findCategoryById(Long id) {
        return findCategoryByIdUseCase.findCategoryById(id)
                .orElseThrow(() -> new CategoryException(id));
    }

    public Category upsertCategory(Category category) {
        return upsertCategoryUseCase.upsertCategory(category);
    }
}
