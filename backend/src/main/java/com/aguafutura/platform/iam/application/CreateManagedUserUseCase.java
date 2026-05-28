package com.aguafutura.platform.iam.application;

import com.aguafutura.platform.core.application.ConflictException;
import com.aguafutura.platform.core.application.ForbiddenOperationException;
import com.aguafutura.platform.core.application.port.AuditLogPort;
import com.aguafutura.platform.core.domain.AuditLog;
import com.aguafutura.platform.iam.application.port.PasswordHasherPort;
import com.aguafutura.platform.iam.application.port.UserRepositoryPort;
import com.aguafutura.platform.iam.domain.User;
import com.aguafutura.platform.iam.domain.UserRole;

import java.util.Set;
import java.util.UUID;

public class CreateManagedUserUseCase {

    private static final Set<UserRole> ADMIN_CREATABLE_ROLES = Set.of(
            UserRole.COORDINATOR,
            UserRole.TECHNICIAN,
            UserRole.AUDITOR,
            UserRole.CITIZEN
    );

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordHasherPort passwordHasherPort;
    private final AuditLogPort auditLogPort;

    public CreateManagedUserUseCase(
            UserRepositoryPort userRepositoryPort,
            PasswordHasherPort passwordHasherPort,
            AuditLogPort auditLogPort
    ) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordHasherPort = passwordHasherPort;
        this.auditLogPort = auditLogPort;
    }

    public User execute(
            UUID actorTenantId,
            UUID actorId,
            UserRole actorRole,
            String correlationId,
            UUID requestedTenantId,
            String fullName,
            String email,
            String password,
            UserRole role
    ) {
        if (actorRole == null) throw new ForbiddenOperationException("Actor role is required");
        if (role == null) throw new IllegalArgumentException("Role is required");
        if (password == null || password.isBlank()) throw new IllegalArgumentException("Temporary password is required");

        UUID targetTenantId = actorRole == UserRole.SUPER_ADMIN ? requestedTenantId : actorTenantId;
        if (targetTenantId == null) throw new IllegalArgumentException("Tenant is required");

        if (actorRole == UserRole.ADMIN && !ADMIN_CREATABLE_ROLES.contains(role)) {
            throw new ForbiddenOperationException("ADMIN cannot create role " + role);
        }
        if (actorRole == UserRole.SUPER_ADMIN && role != UserRole.ADMIN) {
            throw new ForbiddenOperationException("SUPER_ADMIN can create tenant ADMIN users only from this endpoint");
        }
        if (actorRole != UserRole.ADMIN && actorRole != UserRole.SUPER_ADMIN) {
            throw new ForbiddenOperationException("Only ADMIN or SUPER_ADMIN can create managed users");
        }

        if (userRepositoryPort.existsByTenantIdAndEmail(targetTenantId, email)) {
            throw new ConflictException("A user with that email already exists in this tenant");
        }

        User user = User.create(
                targetTenantId,
                fullName,
                email,
                passwordHasherPort.hash(password),
                role
        );
        User saved = userRepositoryPort.save(user);

        auditLogPort.save(AuditLog.create(
                targetTenantId,
                actorId,
                actorRole.name(),
                "USER_CREATED",
                "IAM_USER",
                saved.getId().toString(),
                correlationId
        ));

        return saved;
    }
}
