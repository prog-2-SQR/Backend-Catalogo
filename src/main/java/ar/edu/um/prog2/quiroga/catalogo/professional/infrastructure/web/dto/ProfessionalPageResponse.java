package ar.edu.um.prog2.quiroga.catalogo.professional.infrastructure.web.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Una página de profesionales: los resultados y los datos para pedir la siguiente. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfessionalPageResponse {

    private List<ProfessionalResponse> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}
