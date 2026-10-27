package ar.edu.um.prog2.quiroga.catalogo.professional.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Filtros de la búsqueda (enunciado §4.1) y la página pedida. Un filtro en null no se aplica. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfessionalSearch {

    private Long categoryId;
    private String name;
    private Boolean enabled;
    private int page;
    private int size;

    /** El nombre sin espacios de sobra; vacío cuenta como "sin filtro de nombre". */
    public String normalizedName() {
        if (name == null || name.isBlank()) return null;
        return name.trim();
    }
}
