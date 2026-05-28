package com.aguafutura.platform.iam.application.port;

import com.aguafutura.platform.iam.domain.User;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

public interface UserRepositoryPort {

    User save(User user);

    Optional<User> findByTenantIdAndEmail(UUID tenantId, String email);

    Optional<User> findByTenantIdAndId(UUID tenantId, UUID id);

    List<User> findAllByTenantId(UUID tenantId);

    List<User> findAllByTenantIdAndRole(UUID tenantId, com.aguafutura.platform.iam.domain.UserRole role);

    boolean existsByTenantIdAndEmail(UUID tenantId, String email);
}
