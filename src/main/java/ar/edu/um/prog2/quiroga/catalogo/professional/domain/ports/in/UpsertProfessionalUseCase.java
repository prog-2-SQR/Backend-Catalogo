package ar.edu.um.prog2.quiroga.catalogo.professional.domain.ports.in;

import ar.edu.um.prog2.quiroga.catalogo.professional.domain.model.Professional;

public interface UpsertProfessionalUseCase {

    Professional upsertProfessional(Professional professional);
}
