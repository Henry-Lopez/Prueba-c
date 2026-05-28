package com.aguafutura.platform.iam.api;

import com.aguafutura.platform.iam.domain.UserRole;

import java.util.UUID;

public record UserResponse(
        UUID id,
        UUID tenantId,
        String fullName,
        String email,
        UserRole role,
        boolean enabled
) {}
