package ar.edu.um.prog2.quiroga.catalogo.professional.domain.ports.in;

import ar.edu.um.prog2.quiroga.catalogo.professional.domain.model.ProfessionalPage;
import ar.edu.um.prog2.quiroga.catalogo.professional.domain.model.ProfessionalSearch;

public interface SearchProfessionalsUseCase {

    ProfessionalPage searchProfessionals(ProfessionalSearch search);
}
