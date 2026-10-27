package ar.edu.um.prog2.quiroga.catalogo.professional.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ProfessionalSearchTest {

    @Test
    void theNameIsTrimmedAndBlankMeansNoFilter() {
        assertThat(ProfessionalSearch.builder().name("  Ana Tor ").build().normalizedName()).isEqualTo("Ana Tor");
        assertThat(ProfessionalSearch.builder().name("   ").build().normalizedName()).isNull();
        assertThat(ProfessionalSearch.builder().build().normalizedName()).isNull();
    }

    @Test
    void totalPagesRoundsUp() {
        assertThat(ProfessionalPage.builder().size(10).totalElements(24).build().totalPages()).isEqualTo(3);
        assertThat(ProfessionalPage.builder().size(10).totalElements(20).build().totalPages()).isEqualTo(2);
        assertThat(ProfessionalPage.builder().size(10).totalElements(0).build().totalPages()).isZero();
        assertThat(ProfessionalPage.builder().size(0).totalElements(5).build().totalPages()).isZero();
    }
}
