package com.aguafutura.platform.incidents.api;

import com.aguafutura.platform.incidents.domain.IncidentSeverity;
import com.aguafutura.platform.incidents.domain.IncidentStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public record IncidentResponse(
        UUID id,
        UUID tenantId,
        UUID assetId,
        String title,
        String description,
        IncidentSeverity severity,
        IncidentStatus status,
        LocalDateTime createdAt,
        String assetName,
        String assetCode,
        String displayName
) {
}
