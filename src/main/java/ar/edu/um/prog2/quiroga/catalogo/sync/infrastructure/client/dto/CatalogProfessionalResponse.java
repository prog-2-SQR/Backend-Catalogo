package ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.client.dto;

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
public class CatalogProfessionalResponse {

    private Long id;
    private Long categoryId;
    private String firstName;
    private String lastName;
    private Boolean enabled;
    private Instant createdAt;
    private Instant updatedAt;
}
