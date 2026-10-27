package ar.edu.um.prog2.quiroga.catalogo.professional.infrastructure.persistence.adapter;

import ar.edu.um.prog2.quiroga.catalogo.professional.domain.model.Professional;
import ar.edu.um.prog2.quiroga.catalogo.professional.domain.model.ProfessionalPage;
import ar.edu.um.prog2.quiroga.catalogo.professional.domain.model.ProfessionalSearch;
import ar.edu.um.prog2.quiroga.catalogo.professional.domain.ports.out.ProfessionalRepository;
import ar.edu.um.prog2.quiroga.catalogo.professional.infrastructure.persistence.entity.ProfessionalEntity;
import ar.edu.um.prog2.quiroga.catalogo.professional.infrastructure.persistence.mapper.ProfessionalMapper;
import ar.edu.um.prog2.quiroga.catalogo.professional.infrastructure.persistence.repository.JpaProfessionalRepository;
import ar.edu.um.prog2.quiroga.catalogo.professional.infrastructure.persistence.specification.ProfessionalSpecifications;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JpaProfessionalRepositoryAdapter implements ProfessionalRepository {

    private final JpaProfessionalRepository jpaProfessionalRepository;
    private final ProfessionalMapper professionalMapper;

    @Override
    public Optional<Professional> findProfessionalById(Long id) {
        return jpaProfessionalRepository.findById(id)
                .map(professionalMapper::toDomain);
    }

    @Override
    public List<Professional> findAllProfessionals() {
        return jpaProfessionalRepository.findAll()
                .stream()
                .map(professionalMapper::toDomain)
                .toList();
    }

    @Override
    public Professional upsertProfessional(Professional professional) {
        ProfessionalEntity entity = professionalMapper.toEntity(professional);
        ProfessionalEntity saved = jpaProfessionalRepository.save(entity);
        return professionalMapper.toDomain(saved);
    }

    // Orden estable (apellido, nombre, id): sin orden fijo, una misma página podría devolver resultados distintos.
    @Override
    public ProfessionalPage searchProfessionals(ProfessionalSearch search) {
        PageRequest pageRequest = PageRequest.of(search.getPage(), search.getSize(), Sort.by("lastName", "firstName", "id"));
        Page<ProfessionalEntity> page = jpaProfessionalRepository.findAll(ProfessionalSpecifications.matching(search), pageRequest);
        return ProfessionalPage.builder()
                .content(page.getContent().stream().map(professionalMapper::toDomain).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .build();
    }
}
