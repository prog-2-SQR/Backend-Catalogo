package ar.edu.um.prog2.quiroga.catalogo.professional.infrastructure.web.controller;

import ar.edu.um.prog2.quiroga.catalogo.professional.application.service.ProfessionalService;
import ar.edu.um.prog2.quiroga.catalogo.professional.infrastructure.web.dto.ProfessionalResponse;
import ar.edu.um.prog2.quiroga.catalogo.professional.infrastructure.web.mapper.ProfessionalDtoMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @GetMapping("/{id}")
    public ResponseEntity<ProfessionalResponse> getProfessionalById(@PathVariable Long id) {
        return ResponseEntity.ok(professionalDtoMapper.toResponse(professionalService.findProfessionalById(id)));
    }
}
