package ar.edu.um.prog2.quiroga.catalogo.professional.application.usecases;

import org.springframework.stereotype.Component;

import ar.edu.um.prog2.quiroga.catalogo.professional.domain.model.Professional;
import ar.edu.um.prog2.quiroga.catalogo.professional.domain.ports.in.UpsertProfessionalUseCase;
import ar.edu.um.prog2.quiroga.catalogo.professional.domain.ports.out.ProfessionalRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UpsertProfessionalUseCaseImpl implements UpsertProfessionalUseCase {

    private final ProfessionalRepository professionalRepository;

    @Override
    public Professional upsertProfessional(Professional professional) {
        return professionalRepository.upsertProfessional(professional);
    }
}
