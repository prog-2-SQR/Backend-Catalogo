package ar.edu.um.prog2.quiroga.catalogo.category.domain.model;

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
public class Category {

    private Long id;
    private String name;
    private String description;
    private Boolean enabled;
    private Instant createdAt;
    private Instant updatedAt;
}
