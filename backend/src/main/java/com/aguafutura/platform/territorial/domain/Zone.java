package com.aguafutura.platform.territorial.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public class Zone {

    private UUID id;
    private UUID tenantId;
    private String code;
    private String name;
    private String description;
    private Boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Zone(
            UUID id,
            UUID tenantId,
            String code,
            String name,
            String description,
            Boolean enabled,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        if (id == null) throw new IllegalArgumentException("Zone id is required");
        if (tenantId == null) throw new IllegalArgumentException("Tenant id is required");
        validateCode(code);
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Zone name is required");

        this.id = id;
        this.tenantId = tenantId;
        this.code = normalizeCode(code);
        this.name = name.trim();
        this.description = normalizeDescription(description);
        this.enabled = enabled;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.updatedAt = updatedAt != null ? updatedAt : this.createdAt;
    }

    public static Zone create(UUID tenantId, String code, String name, String description) {
        LocalDateTime now = LocalDateTime.now();
        return new Zone(UUID.randomUUID(), tenantId, code, name, description, true, now, now);
    }

    public Zone update(String code, String name, String description, Boolean enabled) {
        return new Zone(
                id,
                tenantId,
                code,
                name,
                description,
                enabled != null ? enabled : this.enabled,
                createdAt,
                LocalDateTime.now()
        );
    }

    public Zone disable() {
        return update(code, name, description, false);
    }

    public String displayName() {
        return code + " · " + name;
    }

    public static void validateCode(String code) {
        String normalized = normalizeCode(code);
        if (!normalized.matches("^[A-Z0-9-]{3,30}$")) {
            throw new IllegalArgumentException("El código de zona debe ser corto y legible, por ejemplo ZN-NORTE o ZN-CENTRO.");
        }
    }

    public static String normalizeCode(String code) {
        if (code == null) {
            throw new IllegalArgumentException("El código de zona debe ser corto y legible, por ejemplo ZN-NORTE o ZN-CENTRO.");
        }
        return code.trim().toUpperCase();
    }

    private static String normalizeDescription(String description) {
        return description == null || description.isBlank() ? null : description.trim();
    }

    public UUID getId() {
        return id;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
