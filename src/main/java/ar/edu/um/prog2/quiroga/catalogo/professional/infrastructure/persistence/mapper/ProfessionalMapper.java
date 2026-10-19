package ar.edu.um.prog2.quiroga.catalogo.professional.infrastructure.persistence.mapper;

import ar.edu.um.prog2.quiroga.catalogo.professional.domain.model.Professional;
import ar.edu.um.prog2.quiroga.catalogo.professional.infrastructure.persistence.entity.ProfessionalEntity;
import org.springframework.stereotype.Component;

@Component
public class ProfessionalMapper {

    public ProfessionalEntity toEntity(Professional domain) {
        if (domain == null) return null;
        return ProfessionalEntity.builder()
                .id(domain.getId())
                .categoryId(domain.getCategoryId())
                .firstName(domain.getFirstName())
                .lastName(domain.getLastName())
                .enabled(domain.getEnabled())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    public Professional toDomain(ProfessionalEntity entity) {
        if (entity == null) return null;
        return Professional.builder()
                .id(entity.getId())
                .categoryId(entity.getCategoryId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .enabled(entity.getEnabled())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
