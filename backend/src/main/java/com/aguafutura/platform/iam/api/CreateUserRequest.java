package com.aguafutura.platform.iam.api;

import com.aguafutura.platform.iam.domain.UserRole;

import java.util.UUID;

public record CreateUserRequest(
        UUID tenantId,
        String fullName,
        String email,
        String password,
        UserRole role
) {}
