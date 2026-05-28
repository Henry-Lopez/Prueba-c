package com.aguafutura.platform.iam.application;

import com.aguafutura.platform.core.application.ForbiddenOperationException;
import com.aguafutura.platform.iam.application.port.UserRepositoryPort;
import com.aguafutura.platform.iam.domain.User;
import com.aguafutura.platform.iam.domain.UserRole;

import java.util.List;
import java.util.UUID;

public class ListUsersUseCase {

    private final UserRepositoryPort userRepositoryPort;

    public ListUsersUseCase(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    public List<User> execute(UUID actorTenantId, UserRole actorRole, UUID requestedTenantId) {
        if (actorRole == UserRole.SUPER_ADMIN) {
            if (requestedTenantId == null) {
                throw new IllegalArgumentException("tenantId query parameter is required for SUPER_ADMIN");
            }
            return userRepositoryPort.findAllByTenantId(requestedTenantId);
        }
        if (actorRole == UserRole.ADMIN) {
            return userRepositoryPort.findAllByTenantId(actorTenantId);
        }
        throw new ForbiddenOperationException("Only ADMIN or SUPER_ADMIN can list users");
    }
}
