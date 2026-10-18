package ar.edu.um.prog2.quiroga.catalogo.category.application.usecases;

import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import ar.edu.um.prog2.quiroga.catalogo.category.domain.model.Category;
import ar.edu.um.prog2.quiroga.catalogo.category.domain.ports.in.FindCategoryByIdUseCase;
import ar.edu.um.prog2.quiroga.catalogo.category.domain.ports.out.CategoryRepository;

@Component
@RequiredArgsConstructor
public class FindCategoryByIdUseCaseImpl implements FindCategoryByIdUseCase {

    private final CategoryRepository categoryRepository;

    @Override
    public Optional<Category> findCategoryById(Long id) {
        return categoryRepository.findCategoryById(id);
    }
}
