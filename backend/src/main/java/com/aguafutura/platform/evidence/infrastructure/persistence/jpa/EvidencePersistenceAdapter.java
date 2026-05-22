package com.aguafutura.platform.evidence.infrastructure.persistence.jpa;

import com.aguafutura.platform.evidence.application.port.EvidenceRepositoryPort;
import com.aguafutura.platform.evidence.domain.Evidence;
import com.aguafutura.platform.evidence.domain.ReferenceType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class EvidencePersistenceAdapter implements EvidenceRepositoryPort {

    private final EvidenceJpaRepository repository;

    public EvidencePersistenceAdapter(EvidenceJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Evidence save(Evidence evidence) {
        EvidenceJpaEntity entity = EvidenceJpaEntity.fromDomain(evidence);
        EvidenceJpaEntity saved = repository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<Evidence> findById(UUID id) {
        return repository.findById(id).map(EvidenceJpaEntity::toDomain);
    }

    @Override
    public List<Evidence> findAllByTenantIdAndReference(UUID tenantId, ReferenceType type, UUID referenceId) {
        return repository.findByTenantIdAndReferenceTypeAndReferenceIdOrderByCreatedAtDesc(tenantId, type.name(), referenceId)
                .stream()
                .map(EvidenceJpaEntity::toDomain)
                .collect(Collectors.toList());
    }
}
