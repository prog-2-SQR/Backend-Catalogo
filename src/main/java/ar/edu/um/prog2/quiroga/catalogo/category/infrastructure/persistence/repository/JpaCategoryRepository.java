package ar.edu.um.prog2.quiroga.catalogo.category.infrastructure.persistence.repository;

import ar.edu.um.prog2.quiroga.catalogo.category.infrastructure.persistence.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaCategoryRepository extends JpaRepository<CategoryEntity, Long> {}