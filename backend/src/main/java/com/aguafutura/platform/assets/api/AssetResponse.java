package com.aguafutura.platform.assets.api;

import com.aguafutura.platform.assets.domain.AssetType;

import java.time.LocalDateTime;
import java.util.UUID;

public record AssetResponse(
        UUID id,
        UUID tenantId,
        UUID zoneId,
        String code,
        String name,
        AssetType type,
        String locationDescription,
        Boolean enabled,
        LocalDateTime createdAt,
        String zoneName,
        String zoneCode,
        String displayName
) {
}
