package com.aguafutura.platform.incidents.infrastructure.persistence.jpa;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "incident_record")
public class IncidentJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "asset_id", nullable = false)
    private UUID assetId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String severity;

    @Column(nullable = false)
    private String status;

    @Column(name = "reporter_user_id")
    private UUID reporterUserId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected IncidentJpaEntity() {
    }

    public IncidentJpaEntity(
            UUID id,
            UUID tenantId,
            UUID assetId,
            String title,
            String description,
            String severity,
            String status,
            UUID reporterUserId,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.tenantId = tenantId;
        this.assetId = assetId;
        this.title = title;
        this.description = description;
        this.severity = severity;
        this.status = status;
        this.reporterUserId = reporterUserId;
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

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getSeverity() {
        return severity;
    }

    public String getStatus() {
        return status;
    }

    public UUID getReporterUserId() {
        return reporterUserId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
