package ar.edu.um.prog2.quiroga.catalogo.professional.infrastructure.persistence.repository;

import ar.edu.um.prog2.quiroga.catalogo.professional.infrastructure.persistence.entity.ProfessionalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaProfessionalRepository
        extends JpaRepository<ProfessionalEntity, Long>, JpaSpecificationExecutor<ProfessionalEntity> {
}
