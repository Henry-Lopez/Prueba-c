package com.aguafutura.platform.evidence.application;

import com.aguafutura.platform.evidence.application.port.EvidenceRepositoryPort;
import com.aguafutura.platform.evidence.domain.Evidence;

import java.util.UUID;

public class GetEvidenceUseCase {

    private final EvidenceRepositoryPort repository;

    public GetEvidenceUseCase(EvidenceRepositoryPort repository) {
        this.repository = repository;
    }

    public Evidence execute(UUID tenantId, UUID evidenceId) {
        return repository.findByTenantIdAndId(tenantId, evidenceId)
                .orElseThrow(() -> new IllegalArgumentException("Evidence not found for tenant"));
    }
}
