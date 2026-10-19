package ar.edu.um.prog2.quiroga.catalogo.professional.infrastructure.web.mapper;

import static ar.edu.um.prog2.quiroga.catalogo.professional.support.ProfessionalFixtures.anaTorres;
import static org.assertj.core.api.Assertions.assertThat;

import ar.edu.um.prog2.quiroga.catalogo.professional.infrastructure.web.dto.ProfessionalResponse;
import org.junit.jupiter.api.Test;

class ProfessionalDtoMapperTest {

    private final ProfessionalDtoMapper professionalDtoMapper = new ProfessionalDtoMapper();

    @Test
    void toResponseExposesOnlyThePublicFields() {
        ProfessionalResponse response = professionalDtoMapper.toResponse(anaTorres());

        assertThat(response.getId()).isEqualTo(201L);
        assertThat(response.getCategoryId()).isEqualTo(101L);
        assertThat(response.getFirstName()).isEqualTo("Ana");
        assertThat(response.getLastName()).isEqualTo("Torres");
        assertThat(response.getEnabled()).isTrue();
    }

    @Test
    void nullInputReturnsNull() {
        assertThat(professionalDtoMapper.toResponse(null)).isNull();
    }
}
