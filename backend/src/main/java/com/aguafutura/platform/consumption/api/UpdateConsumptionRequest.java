package com.aguafutura.platform.consumption.api;

import com.aguafutura.platform.consumption.domain.UnitType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record UpdateConsumptionRequest(
        LocalDateTime readingDate,
        BigDecimal value,
        UnitType unit
) {
}
