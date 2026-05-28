package com.aguafutura.platform.consumption.infrastructure.persistence.jpa;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "consumption_record")
public class ConsumptionJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "asset_id", nullable = false)
    private UUID assetId;

    @Column(name = "reading_date", nullable = false)
    private LocalDateTime readingDate;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal value;

    @Column(nullable = false)
    private String unit;

    @Column(name = "original_value", precision = 19, scale = 4)
    private BigDecimal originalValue;

    @Column(name = "original_unit")
    private String originalUnit;

    @Column(name = "normalized_volume_m3", precision = 19, scale = 6)
    private BigDecimal normalizedVolumeM3;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected ConsumptionJpaEntity() {
    }

    public ConsumptionJpaEntity(
            UUID id,
            UUID tenantId,
            UUID assetId,
            LocalDateTime readingDate,
            BigDecimal value,
            String unit,
            BigDecimal originalValue,
            String originalUnit,
            BigDecimal normalizedVolumeM3,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.tenantId = tenantId;
        this.assetId = assetId;
        this.readingDate = readingDate;
        this.value = value;
        this.unit = unit;
        this.originalValue = originalValue;
        this.originalUnit = originalUnit;
        this.normalizedVolumeM3 = normalizedVolumeM3;
        this.createdAt = createdAt;
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

    public String getUnit() {
        return unit;
    }

    public BigDecimal getOriginalValue() {
        return originalValue;
    }

    public String getOriginalUnit() {
        return originalUnit;
    }

    public BigDecimal getNormalizedVolumeM3() {
        return normalizedVolumeM3;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
