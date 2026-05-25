package com.aguafutura.platform.territorial.api;

import java.time.LocalDateTime;
import java.util.UUID;

public record ZoneResponse(
        UUID id,
        UUID tenantId,
        String code,
        String name,
        String description,
        Boolean enabled,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String displayName
) {
}
