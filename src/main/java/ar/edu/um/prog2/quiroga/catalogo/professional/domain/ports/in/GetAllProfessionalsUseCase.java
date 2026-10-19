package ar.edu.um.prog2.quiroga.catalogo.professional.domain.ports.in;

import java.util.List;

import ar.edu.um.prog2.quiroga.catalogo.professional.domain.model.Professional;

public interface GetAllProfessionalsUseCase {

    List<Professional> getAllProfessionals();
}
