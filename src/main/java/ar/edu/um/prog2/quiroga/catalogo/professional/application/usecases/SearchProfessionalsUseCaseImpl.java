package ar.edu.um.prog2.quiroga.catalogo.professional.application.usecases;

import ar.edu.um.prog2.quiroga.catalogo.professional.domain.model.ProfessionalPage;
import ar.edu.um.prog2.quiroga.catalogo.professional.domain.model.ProfessionalSearch;
import ar.edu.um.prog2.quiroga.catalogo.professional.domain.ports.in.SearchProfessionalsUseCase;
import ar.edu.um.prog2.quiroga.catalogo.professional.domain.ports.out.ProfessionalAvailability;
import ar.edu.um.prog2.quiroga.catalogo.professional.domain.ports.out.ProfessionalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Búsqueda sobre la copia local (enunciado §6.2: nunca se consulta a la cátedra por cada búsqueda).
 * Con filtro de día, primero se averigua quién atiende ese día y la búsqueda se limita a esos profesionales.
 */
@Component
@RequiredArgsConstructor
public class SearchProfessionalsUseCaseImpl implements SearchProfessionalsUseCase {

    private final ProfessionalRepository professionalRepository;
    private final ProfessionalAvailability professionalAvailability;

    @Override
    public ProfessionalPage searchProfessionals(ProfessionalSearch search) {
        if (search.getDayOfWeek() == null) {
            return professionalRepository.searchProfessionals(search);
        }
        return professionalRepository.searchProfessionals(
                search.restrictedTo(professionalAvailability.findProfessionalIdsAvailableOn(search.getDayOfWeek())));
    }
}
