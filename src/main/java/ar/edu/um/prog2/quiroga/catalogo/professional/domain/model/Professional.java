package ar.edu.um.prog2.quiroga.catalogo.professional.domain.model;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Professional {

    private Long id;
    /** Referencia por ID a la categoría (ADR 0003): este slice no conoce el modelo de categoría. */
    private Long categoryId;
    private String firstName;
    private String lastName;
    private Boolean enabled;
    private Instant createdAt;
    private Instant updatedAt;
}
