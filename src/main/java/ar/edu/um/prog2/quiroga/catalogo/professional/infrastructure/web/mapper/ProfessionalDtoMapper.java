package ar.edu.um.prog2.quiroga.catalogo.professional.infrastructure.web.mapper;

import ar.edu.um.prog2.quiroga.catalogo.professional.domain.model.Professional;
import ar.edu.um.prog2.quiroga.catalogo.professional.domain.model.ProfessionalPage;
import ar.edu.um.prog2.quiroga.catalogo.professional.infrastructure.web.dto.ProfessionalPageResponse;
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

    public ProfessionalPageResponse toPageResponse(ProfessionalPage page) {
        if (page == null) return null;
        return ProfessionalPageResponse.builder()
                .content(page.getContent().stream().map(this::toResponse).toList())
                .page(page.getPage())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.totalPages())
                .build();
    }
}
