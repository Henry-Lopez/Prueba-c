package com.aguafutura.platform.consumption.api;

import com.aguafutura.platform.consumption.domain.UnitType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ConsumptionResponse(
        UUID id,
        UUID tenantId,
        UUID assetId,
        LocalDateTime readingDate,
        BigDecimal value,
        UnitType unit,
        BigDecimal originalValue,
        UnitType originalUnit,
        BigDecimal normalizedVolumeM3,
        LocalDateTime createdAt
) {
}
