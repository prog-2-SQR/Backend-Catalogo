package ar.edu.um.prog2.quiroga.catalogo.professional.application.usecases;

import java.util.List;

import org.springframework.stereotype.Component;

import ar.edu.um.prog2.quiroga.catalogo.professional.domain.model.Professional;
import ar.edu.um.prog2.quiroga.catalogo.professional.domain.ports.in.GetAllProfessionalsUseCase;
import ar.edu.um.prog2.quiroga.catalogo.professional.domain.ports.out.ProfessionalRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GetAllProfessionalsUseCaseImpl implements GetAllProfessionalsUseCase {

    private final ProfessionalRepository professionalRepository;

    @Override
    public List<Professional> getAllProfessionals() {
        return professionalRepository.findAllProfessionals();
    }
}
