package ar.edu.um.prog2.quiroga.catalogo.professional.infrastructure.web.mapper;

import ar.edu.um.prog2.quiroga.catalogo.professional.domain.model.Professional;
import ar.edu.um.prog2.quiroga.catalogo.professional.infrastructure.web.dto.ProfessionalResponse;
import org.springframework.stereotype.Component;

@Component
public class ProfessionalDtoMapper {

    public ProfessionalResponse toResponse(Professional domain) {
        if (domain == null) return null;
        return ProfessionalResponse.builder()
                .id(domain.getId())
                .categoryId(domain.getCategoryId())
                .firstName(domain.getFirstName())
                .lastName(domain.getLastName())
                .enabled(domain.getEnabled())
                .build();
    }
}
