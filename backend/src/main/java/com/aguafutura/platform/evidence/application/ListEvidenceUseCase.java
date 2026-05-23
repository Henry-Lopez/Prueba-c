package com.aguafutura.platform.evidence.application;

import com.aguafutura.platform.evidence.application.port.EvidenceRepositoryPort;
import com.aguafutura.platform.evidence.domain.Evidence;
import com.aguafutura.platform.evidence.domain.ReferenceType;

import java.util.List;
import java.util.UUID;

public class ListEvidenceUseCase {

    private final EvidenceRepositoryPort repository;

    public ListEvidenceUseCase(EvidenceRepositoryPort repository) {
        this.repository = repository;
    }

    public List<Evidence> execute(UUID tenantId, ReferenceType referenceType, UUID referenceId) {
        return repository.findAllByTenantIdAndReference(tenantId, referenceType, referenceId);
    }
}
