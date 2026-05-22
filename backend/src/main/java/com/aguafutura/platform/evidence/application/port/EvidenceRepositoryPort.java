package com.aguafutura.platform.evidence.application.port;

import com.aguafutura.platform.evidence.domain.Evidence;
import com.aguafutura.platform.evidence.domain.ReferenceType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EvidenceRepositoryPort {
    Evidence save(Evidence evidence);
    Optional<Evidence> findById(UUID id);
    List<Evidence> findAllByTenantIdAndReference(UUID tenantId, ReferenceType type, UUID referenceId);
}
