package ar.edu.um.prog2.quiroga.catalogo.professional.domain.model;

import java.time.DayOfWeek;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Filtros de la búsqueda (enunciado §4.1) y la página pedida. Un filtro en null no se aplica.
 * professionalIds no lo elige el usuario: lo completa el caso de uso a partir de dayOfWeek (disponibilidad).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ProfessionalSearch {

    private Long categoryId;
    private String name;
    private Boolean enabled;
    private DayOfWeek dayOfWeek;
    private Set<Long> professionalIds;
    private int page;
    private int size;

    /** El nombre sin espacios de sobra; vacío cuenta como "sin filtro de nombre". */
    public String normalizedName() {
        if (name == null || name.isBlank()) return null;
        return name.trim();
    }

    /** La misma búsqueda, limitada a esos profesionales. */
    public ProfessionalSearch restrictedTo(Set<Long> ids) {
        return toBuilder().professionalIds(Set.copyOf(ids)).build();
    }
}
