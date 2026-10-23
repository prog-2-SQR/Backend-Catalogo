package ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.persistence.entity;

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

/** Una sola fila (id fijo): la versión de la copia local. */
@Entity
@Table(name = "sync_state")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SyncStateEntity {

    public static final short SINGLE_ROW_ID = 1;

    @Id
    private Short id;
    @Column(nullable = false)
    private Long snapshotVersion;
    @Column(nullable = false)
    private Instant appliedAt;
}
