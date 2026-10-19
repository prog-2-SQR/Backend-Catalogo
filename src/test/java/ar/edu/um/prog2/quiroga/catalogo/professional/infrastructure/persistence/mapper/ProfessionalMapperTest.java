package ar.edu.um.prog2.quiroga.catalogo.professional.infrastructure.persistence.mapper;

import static ar.edu.um.prog2.quiroga.catalogo.professional.support.ProfessionalFixtures.anaTorres;
import static org.assertj.core.api.Assertions.assertThat;

import ar.edu.um.prog2.quiroga.catalogo.professional.domain.model.Professional;
import ar.edu.um.prog2.quiroga.catalogo.professional.infrastructure.persistence.entity.ProfessionalEntity;
import org.junit.jupiter.api.Test;

class ProfessionalMapperTest {

    private final ProfessionalMapper professionalMapper = new ProfessionalMapper();

    @Test
    void domainToEntityAndBackKeepsAllFields() {
        Professional original = anaTorres();

        ProfessionalEntity entity = professionalMapper.toEntity(original);
        Professional back = professionalMapper.toDomain(entity);

        assertThat(entity.getCategoryId()).isEqualTo(101L);
        assertThat(back).usingRecursiveComparison().isEqualTo(original);
    }

    @Test
    void nullInputsReturnNull() {
        assertThat(professionalMapper.toEntity(null)).isNull();
        assertThat(professionalMapper.toDomain(null)).isNull();
    }
}
