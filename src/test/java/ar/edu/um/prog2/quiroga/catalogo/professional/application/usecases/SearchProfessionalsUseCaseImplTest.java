package ar.edu.um.prog2.quiroga.catalogo.professional.application.usecases;

import static ar.edu.um.prog2.quiroga.catalogo.professional.support.ProfessionalFixtures.anaTorres;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import ar.edu.um.prog2.quiroga.catalogo.professional.domain.model.ProfessionalPage;
import ar.edu.um.prog2.quiroga.catalogo.professional.domain.model.ProfessionalSearch;
import ar.edu.um.prog2.quiroga.catalogo.professional.domain.ports.out.ProfessionalRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SearchProfessionalsUseCaseImplTest {

    @Mock
    private ProfessionalRepository professionalRepository;

    @InjectMocks
    private SearchProfessionalsUseCaseImpl searchProfessionalsUseCase;

    @Test
    void searchesTheLocalCopy() {
        ProfessionalSearch search = ProfessionalSearch.builder().categoryId(101L).page(0).size(20).build();
        ProfessionalPage page = ProfessionalPage.builder().content(List.of(anaTorres())).page(0).size(20).totalElements(1).build();
        when(professionalRepository.searchProfessionals(search)).thenReturn(page);

        assertThat(searchProfessionalsUseCase.searchProfessionals(search)).isSameAs(page);
    }
}
