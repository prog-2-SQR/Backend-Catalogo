package ar.edu.um.prog2.quiroga.catalogo.professional.application.usecases;

import java.util.Optional;

import org.springframework.stereotype.Component;

import ar.edu.um.prog2.quiroga.catalogo.professional.domain.model.Professional;
import ar.edu.um.prog2.quiroga.catalogo.professional.domain.ports.in.FindProfessionalByIdUseCase;
import ar.edu.um.prog2.quiroga.catalogo.professional.domain.ports.out.ProfessionalRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FindProfessionalByIdUseCaseImpl implements FindProfessionalByIdUseCase {

    private final ProfessionalRepository professionalRepository;

    @Override
    public Optional<Professional> findProfessionalById(Long id) {
        return professionalRepository.findProfessionalById(id);
    }
}
