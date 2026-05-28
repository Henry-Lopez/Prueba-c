package com.aguafutura.platform.iam.infrastructure.persistence.jpa;

import com.aguafutura.platform.iam.application.port.UserRepositoryPort;
import com.aguafutura.platform.iam.domain.User;
import com.aguafutura.platform.iam.domain.UserRole;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

@Component
public class UserPersistenceAdapter implements UserRepositoryPort {

    private final UserJpaRepository repository;

    public UserPersistenceAdapter(UserJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public User save(User user) {
        UserJpaEntity entity = new UserJpaEntity(
                user.getId(),
                user.getTenantId(),
                user.getFullName(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getRole().name(),
                user.isEnabled()
        );

        return toDomain(repository.save(entity));
    }

    @Override
    public Optional<User> findByTenantIdAndEmail(UUID tenantId, String email) {
        return repository.findByTenantIdAndEmail(tenantId, email.toLowerCase().trim())
                .map(this::toDomain);
    }

    @Override
    public Optional<User> findByTenantIdAndId(UUID tenantId, UUID id) {
        return repository.findByTenantIdAndId(tenantId, id).map(this::toDomain);
    }

    @Override
    public List<User> findAllByTenantId(UUID tenantId) {
        return repository.findByTenantIdOrderByCreatedAtDesc(tenantId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<User> findAllByTenantIdAndRole(UUID tenantId, UserRole role) {
        return repository.findByTenantIdAndRoleOrderByFullNameAsc(tenantId, role.name())
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public boolean existsByTenantIdAndEmail(UUID tenantId, String email) {
        return repository.existsByTenantIdAndEmail(tenantId, email.toLowerCase().trim());
    }

    private User toDomain(UserJpaEntity entity) {
        return new User(
                entity.getId(),
                entity.getTenantId(),
                entity.getFullName(),
                entity.getEmail(),
                entity.getPasswordHash(),
                UserRole.valueOf(entity.getRole()),
                entity.isEnabled()
        );
    }
}
