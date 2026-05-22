package com.aguafutura.platform.evidence.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EvidenceJpaRepository extends JpaRepository<EvidenceJpaEntity, UUID> {
    List<EvidenceJpaEntity> findByTenantIdAndReferenceTypeAndReferenceIdOrderByCreatedAtDesc(
            UUID tenantId,
            String referenceType,
            UUID referenceId
    );
}
