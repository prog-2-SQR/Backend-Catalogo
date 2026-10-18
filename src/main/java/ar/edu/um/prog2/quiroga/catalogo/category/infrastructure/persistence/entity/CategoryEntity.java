package ar.edu.um.prog2.quiroga.catalogo.category.infrastructure.persistence.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryEntity {

    @Id
    private Long id;
    @Column(nullable = false)
    private String name;
    private String description;
    @Column(nullable = false)
    private Boolean enabled;
    @Column(nullable = false)
    private Instant createdAt;
    @Column(nullable = false)
    private Instant updatedAt;
}
