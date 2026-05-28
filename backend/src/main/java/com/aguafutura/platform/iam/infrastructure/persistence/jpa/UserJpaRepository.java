package com.aguafutura.platform.iam.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, UUID> {

    Optional<UserJpaEntity> findByTenantIdAndEmail(UUID tenantId, String email);

    Optional<UserJpaEntity> findByTenantIdAndId(UUID tenantId, UUID id);

    List<UserJpaEntity> findByTenantIdOrderByCreatedAtDesc(UUID tenantId);

    List<UserJpaEntity> findByTenantIdAndRoleOrderByFullNameAsc(UUID tenantId, String role);

    boolean existsByTenantIdAndEmail(UUID tenantId, String email);
}
