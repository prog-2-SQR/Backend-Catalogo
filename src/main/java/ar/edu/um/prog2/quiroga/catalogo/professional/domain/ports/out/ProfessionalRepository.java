package ar.edu.um.prog2.quiroga.catalogo.professional.domain.ports.out;

import java.util.List;
import java.util.Optional;

import ar.edu.um.prog2.quiroga.catalogo.professional.domain.model.Professional;
import ar.edu.um.prog2.quiroga.catalogo.professional.domain.model.ProfessionalPage;
import ar.edu.um.prog2.quiroga.catalogo.professional.domain.model.ProfessionalSearch;

public interface ProfessionalRepository {

    Optional<Professional> findProfessionalById(Long id);

    List<Professional> findAllProfessionals();

    Professional upsertProfessional(Professional professional);

    ProfessionalPage searchProfessionals(ProfessionalSearch search);
}
