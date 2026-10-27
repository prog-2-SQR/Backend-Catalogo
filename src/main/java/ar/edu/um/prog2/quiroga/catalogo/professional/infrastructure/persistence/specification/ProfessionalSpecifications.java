package ar.edu.um.prog2.quiroga.catalogo.professional.infrastructure.persistence.specification;

import java.util.Locale;
import java.util.Set;

import ar.edu.um.prog2.quiroga.catalogo.professional.domain.model.ProfessionalSearch;
import ar.edu.um.prog2.quiroga.catalogo.professional.infrastructure.persistence.entity.ProfessionalEntity;
import jakarta.persistence.criteria.Expression;
import org.springframework.data.jpa.domain.Specification;

/**
 * Filtros de la búsqueda como Specification de JPA: cada filtro presente suma una condición (AND).
 * Los valores van como parámetros de la consulta, nunca concatenados al SQL.
 */
public final class ProfessionalSpecifications {

    private static final char ESCAPE = '\\';

    private ProfessionalSpecifications() {
    }

    public static Specification<ProfessionalEntity> matching(ProfessionalSearch search) {
        Specification<ProfessionalEntity> specification = Specification.unrestricted();
        if (search.getCategoryId() != null) {
            specification = specification.and(inCategory(search.getCategoryId()));
        }
        if (search.normalizedName() != null) {
            specification = specification.and(nameContains(search.normalizedName()));
        }
        if (search.getEnabled() != null) {
            specification = specification.and(enabled(search.getEnabled()));
        }
        if (search.getProfessionalIds() != null) {
            specification = specification.and(idIn(search.getProfessionalIds()));
        }
        return specification;
    }

    static Specification<ProfessionalEntity> inCategory(Long categoryId) {
        return (root, query, cb) -> cb.equal(root.get("categoryId"), categoryId);
    }

    // "ana", "torres" o "ana tor" encuentran a Ana Torres: busca en "nombre apellido" sin importar mayúsculas.
    static Specification<ProfessionalEntity> nameContains(String name) {
        return (root, query, cb) -> {
            Expression<String> fullName = cb.lower(cb.concat(cb.concat(root.get("firstName"), " "), root.get("lastName")));
            return cb.like(fullName, "%" + escapeLike(name.toLowerCase(Locale.ROOT)) + "%", ESCAPE);
        };
    }

    static Specification<ProfessionalEntity> enabled(Boolean enabled) {
        return (root, query, cb) -> cb.equal(root.get("enabled"), enabled);
    }

    // Sin IDs (nadie atiende ese día) no hay resultados: "IN ()" no es SQL válido, se usa una condición falsa.
    static Specification<ProfessionalEntity> idIn(Set<Long> ids) {
        return (root, query, cb) -> ids.isEmpty() ? cb.disjunction() : root.get("id").in(ids);
    }

    // % y _ son comodines de LIKE: si el usuario los escribe, se buscan como texto.
    private static String escapeLike(String value) {
        return value.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }
}
