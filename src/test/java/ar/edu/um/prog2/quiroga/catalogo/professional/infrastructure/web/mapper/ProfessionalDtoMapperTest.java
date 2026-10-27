package ar.edu.um.prog2.quiroga.catalogo.professional.infrastructure.web.mapper;

import static ar.edu.um.prog2.quiroga.catalogo.professional.support.ProfessionalFixtures.anaTorres;
import static ar.edu.um.prog2.quiroga.catalogo.professional.support.ProfessionalFixtures.emilianoOrtiz;
import static org.assertj.core.api.Assertions.assertThat;

import ar.edu.um.prog2.quiroga.catalogo.professional.domain.model.ProfessionalPage;
import ar.edu.um.prog2.quiroga.catalogo.professional.infrastructure.web.dto.ProfessionalPageResponse;
import ar.edu.um.prog2.quiroga.catalogo.professional.infrastructure.web.dto.ProfessionalResponse;
import java.util.List;
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

    @Test
    void toPageResponseMapsTheContentAndThePagination() {
        ProfessionalPage page = ProfessionalPage.builder()
                .content(List.of(anaTorres(), emilianoOrtiz())).page(1).size(2).totalElements(5).build();

        ProfessionalPageResponse response = professionalDtoMapper.toPageResponse(page);

        assertThat(response.getContent()).extracting(ProfessionalResponse::getId).containsExactly(201L, 224L);
        assertThat(response.getPage()).isEqualTo(1);
        assertThat(response.getSize()).isEqualTo(2);
        assertThat(response.getTotalElements()).isEqualTo(5);
        assertThat(response.getTotalPages()).isEqualTo(3);
        assertThat(professionalDtoMapper.toPageResponse(null)).isNull();
    }
}
