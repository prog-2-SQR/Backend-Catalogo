package ar.edu.um.prog2.quiroga.catalogo.professional.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.DayOfWeek;
import java.util.Set;

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

    @Test
    void restrictedToKeepsTheFiltersAndAddsTheIds() {
        ProfessionalSearch search = ProfessionalSearch.builder()
                .categoryId(101L).dayOfWeek(DayOfWeek.MONDAY).page(1).size(10).build();

        ProfessionalSearch restricted = search.restrictedTo(Set.of(201L, 202L));

        assertThat(restricted.getProfessionalIds()).containsExactlyInAnyOrder(201L, 202L);
        assertThat(restricted.getCategoryId()).isEqualTo(101L);
        assertThat(restricted.getPage()).isEqualTo(1);
        assertThat(search.getProfessionalIds()).isNull();
    }
}
