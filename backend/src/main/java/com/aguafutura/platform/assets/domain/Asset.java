package com.aguafutura.platform.assets.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public class Asset {

    private final UUID id;
    private final UUID tenantId;
    private final UUID zoneId;
    private final String code;
    private final String name;
    private final AssetType type;
    private final String locationDescription;
    private final Boolean enabled;
    private final LocalDateTime createdAt;

    public Asset(
            UUID id,
            UUID tenantId,
            UUID zoneId,
            String code,
            String name,
            AssetType type,
            String locationDescription,
            Boolean enabled,
            LocalDateTime createdAt
    ) {
        if (id == null) throw new IllegalArgumentException("Asset id is required");
        if (tenantId == null) throw new IllegalArgumentException("Tenant id is required");
        if (zoneId == null) throw new IllegalArgumentException("Zone id is required");
        if (code == null || code.isBlank()) throw new IllegalArgumentException("Asset code is required");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Asset name is required");
        if (type == null) throw new IllegalArgumentException("Asset type is required");

        this.id = id;
        this.tenantId = tenantId;
        this.zoneId = zoneId;
        this.code = code.trim().toUpperCase();
        this.name = name.trim();
        this.type = type;
        this.locationDescription = locationDescription;
        this.enabled = enabled;
        this.createdAt = createdAt;
    }

    public static Asset create(
            UUID tenantId,
            UUID zoneId,
            String code,
            String name,
            AssetType type,
            String locationDescription
    ) {
        return new Asset(
                UUID.randomUUID(),
                tenantId,
                zoneId,
                code,
                name,
                type,
                locationDescription,
                true,
                LocalDateTime.now()
        );
    }

    public Asset update(
            UUID zoneId,
            String code,
            String name,
            AssetType type,
            String locationDescription,
            Boolean enabled
    ) {
        return new Asset(
                id,
                tenantId,
                zoneId,
                code,
                name,
                type,
                locationDescription,
                enabled != null ? enabled : this.enabled,
                createdAt
        );
    }

    public Asset disable() {
        return update(zoneId, code, name, type, locationDescription, false);
    }

    public String displayName() {
        return code + " · " + name;
    }

    public UUID getId() {
        return id;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public UUID getZoneId() {
        return zoneId;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public AssetType getType() {
        return type;
    }

    public String getLocationDescription() {
        return locationDescription;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
