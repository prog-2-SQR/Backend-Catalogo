package ar.edu.um.prog2.quiroga.catalogo.professional.domain.ports.in;

import java.util.Optional;

import ar.edu.um.prog2.quiroga.catalogo.professional.domain.model.Professional;

public interface FindProfessionalByIdUseCase {

    Optional<Professional> findProfessionalById(Long id);
}
