package com.aguafutura.platform.core.infrastructure.persistence.jpa;

import com.aguafutura.platform.core.application.port.TenantRepositoryPort;
import com.aguafutura.platform.core.domain.Tenant;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class TenantPersistenceAdapter implements TenantRepositoryPort {

    private final TenantJpaRepository tenantJpaRepository;

    public TenantPersistenceAdapter(TenantJpaRepository tenantJpaRepository) {
        this.tenantJpaRepository = tenantJpaRepository;
    }

    @Override
    public Tenant save(Tenant tenant) {
        return tenantJpaRepository.save(TenantJpaEntity.fromDomain(tenant)).toDomain();
    }

    @Override
    public List<Tenant> findAll() {
        return tenantJpaRepository.findAll()
                .stream()
                .map(TenantJpaEntity::toDomain)
                .toList();
    }

    @Override
    public Optional<Tenant> findById(UUID id) {
        return tenantJpaRepository.findById(id)
                .map(TenantJpaEntity::toDomain);
    }

    @Override
    public boolean existsByCode(String code) {
        return tenantJpaRepository.existsByCode(code);
    }
}
