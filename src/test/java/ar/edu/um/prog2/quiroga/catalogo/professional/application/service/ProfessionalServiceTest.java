package ar.edu.um.prog2.quiroga.catalogo.professional.application.service;

import static ar.edu.um.prog2.quiroga.catalogo.professional.support.ProfessionalFixtures.anaTorres;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import ar.edu.um.prog2.quiroga.catalogo.professional.application.exception.ProfessionalException;
import ar.edu.um.prog2.quiroga.catalogo.professional.domain.model.Professional;
import ar.edu.um.prog2.quiroga.catalogo.professional.domain.ports.in.FindProfessionalByIdUseCase;
import ar.edu.um.prog2.quiroga.catalogo.professional.domain.ports.in.GetAllProfessionalsUseCase;
import ar.edu.um.prog2.quiroga.catalogo.professional.domain.ports.in.UpsertProfessionalUseCase;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProfessionalServiceTest {

    @Mock
    private GetAllProfessionalsUseCase getAllProfessionalsUseCase;
    @Mock
    private FindProfessionalByIdUseCase findProfessionalByIdUseCase;
    @Mock
    private UpsertProfessionalUseCase upsertProfessionalUseCase;

    @InjectMocks
    private ProfessionalService professionalService;

    @Test
    void getAllProfessionalsDelegatesToTheUseCase() {
        when(getAllProfessionalsUseCase.getAllProfessionals()).thenReturn(List.of(anaTorres()));

        assertThat(professionalService.getAllProfessionals()).hasSize(1);
    }

    @Test
    void findProfessionalByIdReturnsTheProfessional() {
        when(findProfessionalByIdUseCase.findProfessionalById(201L)).thenReturn(Optional.of(anaTorres()));

        Professional result = professionalService.findProfessionalById(201L);

        assertThat(result.getLastName()).isEqualTo("Torres");
    }

    @Test
    void findProfessionalByIdThrowsProfessionalExceptionWhenItDoesNotExist() {
        when(findProfessionalByIdUseCase.findProfessionalById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> professionalService.findProfessionalById(999L))
                .isInstanceOf(ProfessionalException.class)
                .hasMessageContaining("999");
    }

    @Test
    void upsertProfessionalDelegatesToTheUseCase() {
        Professional professional = anaTorres();
        when(upsertProfessionalUseCase.upsertProfessional(professional)).thenReturn(professional);

        assertThat(professionalService.upsertProfessional(professional)).isSameAs(professional);
    }

    @Test
    void professionalExceptionWithoutIdHasAGenericMessage() {
        assertThat(new ProfessionalException()).hasMessage("Profesional no encontrado");
    }
}
