package ar.edu.um.prog2.quiroga.catalogo.professional.domain.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Una página de resultados. Modelo propio: el dominio no usa el Page de Spring Data. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfessionalPage {

    @Builder.Default
    private List<Professional> content = List.of();
    private int page;
    private int size;
    private long totalElements;

    public int totalPages() {
        return size == 0 ? 0 : (int) ((totalElements + size - 1) / size);
    }
}
