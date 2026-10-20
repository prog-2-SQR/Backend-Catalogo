package ar.edu.um.prog2.quiroga.catalogo.category.infrastructure.web.controller;

import ar.edu.um.prog2.quiroga.catalogo.category.application.service.CategoryService;
import ar.edu.um.prog2.quiroga.catalogo.category.infrastructure.web.dto.CategoryResponse;
import ar.edu.um.prog2.quiroga.catalogo.category.infrastructure.web.mapper.CategoryDtoMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryDtoMapper categoryDtoMapper;

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        List<CategoryResponse> categories = categoryService.getAllCategories()
                .stream()
                .map(categoryDtoMapper::toResponse)
                .toList();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryDtoMapper.toResponse(categoryService.findCategoryById(id)));
    }
}
