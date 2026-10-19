package ar.edu.um.prog2.quiroga.catalogo.professional.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import ar.edu.um.prog2.quiroga.catalogo.professional.application.exception.ProfessionalException;
import ar.edu.um.prog2.quiroga.catalogo.professional.domain.model.Professional;
import ar.edu.um.prog2.quiroga.catalogo.professional.domain.ports.in.FindProfessionalByIdUseCase;
import ar.edu.um.prog2.quiroga.catalogo.professional.domain.ports.in.GetAllProfessionalsUseCase;
import ar.edu.um.prog2.quiroga.catalogo.professional.domain.ports.in.UpsertProfessionalUseCase;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfessionalService {

    private final GetAllProfessionalsUseCase getAllProfessionalsUseCase;
    private final FindProfessionalByIdUseCase findProfessionalByIdUseCase;
    private final UpsertProfessionalUseCase upsertProfessionalUseCase;

    public List<Professional> getAllProfessionals() {
        return getAllProfessionalsUseCase.getAllProfessionals();
    }

    public Professional findProfessionalById(Long id) {
        return findProfessionalByIdUseCase.findProfessionalById(id)
                .orElseThrow(() -> new ProfessionalException(id));
    }

    public Professional upsertProfessional(Professional professional) {
        return upsertProfessionalUseCase.upsertProfessional(professional);
    }
}
