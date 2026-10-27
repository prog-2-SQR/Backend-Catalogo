package ar.edu.um.prog2.quiroga.catalogo.professional.infrastructure.web.controller;

import ar.edu.um.prog2.quiroga.catalogo.professional.application.service.ProfessionalService;
import ar.edu.um.prog2.quiroga.catalogo.professional.domain.model.ProfessionalSearch;
import ar.edu.um.prog2.quiroga.catalogo.professional.infrastructure.web.dto.ProfessionalPageResponse;
import ar.edu.um.prog2.quiroga.catalogo.professional.infrastructure.web.dto.ProfessionalResponse;
import ar.edu.um.prog2.quiroga.catalogo.professional.infrastructure.web.mapper.ProfessionalDtoMapper;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/professionals")
@RequiredArgsConstructor
public class ProfessionalController {

    private final ProfessionalService professionalService;
    private final ProfessionalDtoMapper professionalDtoMapper;

    @GetMapping
    public ResponseEntity<List<ProfessionalResponse>> getAllProfessionals() {
        List<ProfessionalResponse> professionals = professionalService.getAllProfessionals()
                .stream()
                .map(professionalDtoMapper::toResponse)
                .toList();
        return ResponseEntity.ok(professionals);
    }

    // Búsqueda paginada (enunciado §4.1): todos los filtros son opcionales y se combinan con AND.
    // Parámetros fuera de rango → 400 VALIDATION_ERROR (validación de métodos de Spring MVC).
    @GetMapping("/search")
    public ResponseEntity<ProfessionalPageResponse> searchProfessionals(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) @Size(max = 100) String name,
            @RequestParam(required = false) Boolean enabled,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        ProfessionalSearch search = ProfessionalSearch.builder()
                .categoryId(categoryId).name(name).enabled(enabled).page(page).size(size)
                .build();
        return ResponseEntity.ok(professionalDtoMapper.toPageResponse(professionalService.searchProfessionals(search)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfessionalResponse> getProfessionalById(@PathVariable Long id) {
        return ResponseEntity.ok(professionalDtoMapper.toResponse(professionalService.findProfessionalById(id)));
    }
}
