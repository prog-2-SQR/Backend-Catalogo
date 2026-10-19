package ar.edu.um.prog2.quiroga.catalogo.professional.application.usecases;

import static ar.edu.um.prog2.quiroga.catalogo.professional.support.ProfessionalFixtures.anaTorres;
import static ar.edu.um.prog2.quiroga.catalogo.professional.support.ProfessionalFixtures.emilianoOrtiz;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ar.edu.um.prog2.quiroga.catalogo.professional.domain.model.Professional;
import ar.edu.um.prog2.quiroga.catalogo.professional.domain.ports.out.ProfessionalRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProfessionalUseCasesTest {

    @Mock
    private ProfessionalRepository professionalRepository;

    @Test
    void getAllProfessionalsReturnsWhatTheRepositoryHas() {
        when(professionalRepository.findAllProfessionals()).thenReturn(List.of(anaTorres(), emilianoOrtiz()));

        List<Professional> result = new GetAllProfessionalsUseCaseImpl(professionalRepository).getAllProfessionals();

        assertThat(result).extracting(Professional::getId).containsExactly(201L, 224L);
    }

    @Test
    void findProfessionalByIdReturnsTheProfessionalWhenItExists() {
        when(professionalRepository.findProfessionalById(201L)).thenReturn(Optional.of(anaTorres()));

        Optional<Professional> result = new FindProfessionalByIdUseCaseImpl(professionalRepository).findProfessionalById(201L);

        assertThat(result).get().extracting(Professional::getCategoryId).isEqualTo(101L);
    }

    @Test
    void findProfessionalByIdReturnsEmptyWhenItDoesNotExist() {
        when(professionalRepository.findProfessionalById(999L)).thenReturn(Optional.empty());

        Optional<Professional> result = new FindProfessionalByIdUseCaseImpl(professionalRepository).findProfessionalById(999L);

        assertThat(result).isEmpty();
    }

    @Test
    void upsertProfessionalDelegatesToTheRepository() {
        Professional professional = anaTorres();
        when(professionalRepository.upsertProfessional(professional)).thenReturn(professional);

        Professional result = new UpsertProfessionalUseCaseImpl(professionalRepository).upsertProfessional(professional);

        assertThat(result).isSameAs(professional);
        verify(professionalRepository).upsertProfessional(professional);
    }
}
