package ar.edu.um.prog2.quiroga.catalogo.professional.infrastructure.persistence.adapter;

import ar.edu.um.prog2.quiroga.catalogo.professional.domain.model.Professional;
import ar.edu.um.prog2.quiroga.catalogo.professional.domain.ports.out.ProfessionalRepository;
import ar.edu.um.prog2.quiroga.catalogo.professional.infrastructure.persistence.entity.ProfessionalEntity;
import ar.edu.um.prog2.quiroga.catalogo.professional.infrastructure.persistence.mapper.ProfessionalMapper;
import ar.edu.um.prog2.quiroga.catalogo.professional.infrastructure.persistence.repository.JpaProfessionalRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
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
}
