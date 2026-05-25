package com.aguafutura.platform.core.infrastructure.persistence.jpa;

import com.aguafutura.platform.core.application.port.AuditLogPort;
import com.aguafutura.platform.core.domain.AuditLog;
import org.springframework.stereotype.Component;

@Component
public class AuditLogPersistenceAdapter implements AuditLogPort {

    private final AuditLogJpaRepository repository;

    public AuditLogPersistenceAdapter(AuditLogJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public AuditLog save(AuditLog auditLog) {
        return repository.save(AuditLogJpaEntity.fromDomain(auditLog)).toDomain();
    }
}
