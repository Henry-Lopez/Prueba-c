package com.aguafutura.platform.consumption.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Consumption {

    private final UUID id;
    private final UUID tenantId;
    private final UUID assetId;
    private final LocalDateTime readingDate;
    private final BigDecimal value;
    private final UnitType unit;
    private final LocalDateTime createdAt;

    public Consumption(
            UUID id,
            UUID tenantId,
            UUID assetId,
            LocalDateTime readingDate,
            BigDecimal value,
            UnitType unit,
            LocalDateTime createdAt
    ) {
        if (id == null) throw new IllegalArgumentException("Consumption id is required");
        if (tenantId == null) throw new IllegalArgumentException("Tenant id is required");
        if (assetId == null) throw new IllegalArgumentException("Asset id is required");
        if (readingDate == null) throw new IllegalArgumentException("Reading date is required");
        if (value == null || value.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Value must be a positive number or zero");
        if (unit == null) throw new IllegalArgumentException("Unit type is required");

        this.id = id;
        this.tenantId = tenantId;
        this.assetId = assetId;
        this.readingDate = readingDate;
        this.value = value;
        this.unit = unit;
        this.createdAt = createdAt;
    }

    public static Consumption create(
            UUID tenantId,
            UUID assetId,
            LocalDateTime readingDate,
            BigDecimal value,
            UnitType unit
    ) {
        return new Consumption(
                UUID.randomUUID(),
                tenantId,
                assetId,
                readingDate,
                value,
                unit,
                LocalDateTime.now()
        );
    }

    public Consumption update(LocalDateTime readingDate, BigDecimal value, UnitType unit) {
        return new Consumption(
                id,
                tenantId,
                assetId,
                readingDate,
                value,
                unit,
                createdAt
        );
    }

    public UUID getId() {
        return id;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public UUID getAssetId() {
        return assetId;
    }

    public LocalDateTime getReadingDate() {
        return readingDate;
    }

    public BigDecimal getValue() {
        return value;
    }

    public UnitType getUnit() {
        return unit;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
