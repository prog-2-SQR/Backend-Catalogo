package ar.edu.um.prog2.quiroga.catalogo.category.application.usecases;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import ar.edu.um.prog2.quiroga.catalogo.category.domain.model.Category;
import ar.edu.um.prog2.quiroga.catalogo.category.domain.ports.in.GetAllCategoriesUseCase;
import ar.edu.um.prog2.quiroga.catalogo.category.domain.ports.out.CategoryRepository;

@Component
@RequiredArgsConstructor
public class GetAllCategoriesUseCaseImpl implements GetAllCategoriesUseCase {

    private final CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAllCategories();
    }
}
