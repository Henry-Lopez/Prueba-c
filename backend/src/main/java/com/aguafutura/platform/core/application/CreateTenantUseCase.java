package com.aguafutura.platform.core.application;

import com.aguafutura.platform.core.application.port.TenantRepositoryPort;
import com.aguafutura.platform.core.domain.Tenant;

import java.time.LocalDateTime;
import java.util.UUID;

public class CreateTenantUseCase {

    private final TenantRepositoryPort tenantRepositoryPort;

    public CreateTenantUseCase(TenantRepositoryPort tenantRepositoryPort) {
        this.tenantRepositoryPort = tenantRepositoryPort;
    }

    public Tenant execute(String code, String name) {
        if (code == null || code.isBlank()) throw new IllegalArgumentException("Tenant code is required");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Tenant name is required");
        String normalizedCode = code.trim().toUpperCase();
        if (tenantRepositoryPort.existsByCode(normalizedCode)) {
            throw new ConflictException("Tenant code already exists");
        }
        LocalDateTime now = LocalDateTime.now();
        return tenantRepositoryPort.save(new Tenant(
                UUID.randomUUID(),
                normalizedCode,
                name.trim(),
                "ACTIVE",
                now,
                now,
                0
        ));
    }
}
