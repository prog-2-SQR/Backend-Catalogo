package ar.edu.um.prog2.quiroga.catalogo.category.application.usecases;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import ar.edu.um.prog2.quiroga.catalogo.category.domain.model.Category;
import ar.edu.um.prog2.quiroga.catalogo.category.domain.ports.in.UpsertCategoryUseCase;
import ar.edu.um.prog2.quiroga.catalogo.category.domain.ports.out.CategoryRepository;

@Component
@RequiredArgsConstructor
public class UpsertCategoryUseCaseImpl implements UpsertCategoryUseCase {

    private final CategoryRepository categoryRepository;

    @Override
    public Category upsertCategory(Category category) {
        return categoryRepository.upsertCategory(category);
    }
}
