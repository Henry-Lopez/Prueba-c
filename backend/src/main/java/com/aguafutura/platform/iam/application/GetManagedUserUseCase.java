package com.aguafutura.platform.iam.application;

import com.aguafutura.platform.core.application.ForbiddenOperationException;
import com.aguafutura.platform.core.application.ResourceNotFoundException;
import com.aguafutura.platform.iam.application.port.UserRepositoryPort;
import com.aguafutura.platform.iam.domain.User;
import com.aguafutura.platform.iam.domain.UserRole;

import java.util.UUID;

public class GetManagedUserUseCase {

    private final UserRepositoryPort userRepositoryPort;

    public GetManagedUserUseCase(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    public User execute(UUID actorTenantId, UserRole actorRole, UUID requestedTenantId, UUID userId) {
        UUID targetTenantId = actorRole == UserRole.SUPER_ADMIN ? requestedTenantId : actorTenantId;
        if (targetTenantId == null) throw new IllegalArgumentException("tenantId query parameter is required for SUPER_ADMIN");
        if (actorRole != UserRole.ADMIN && actorRole != UserRole.SUPER_ADMIN) {
            throw new ForbiddenOperationException("Only ADMIN or SUPER_ADMIN can view managed users");
        }
        return userRepositoryPort.findByTenantIdAndId(targetTenantId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
