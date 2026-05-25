package com.aguafutura.platform.incidents.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public class Incident {

    private final UUID id;
    private final UUID tenantId;
    private final UUID assetId;
    private final String title;
    private final String description;
    private final IncidentSeverity severity;
    private IncidentStatus status;
    private final LocalDateTime createdAt;

    public Incident(
            UUID id,
            UUID tenantId,
            UUID assetId,
            String title,
            String description,
            IncidentSeverity severity,
            IncidentStatus status,
            LocalDateTime createdAt
    ) {
        if (id == null) throw new IllegalArgumentException("Incident id is required");
        if (tenantId == null) throw new IllegalArgumentException("Tenant id is required");
        if (assetId == null) throw new IllegalArgumentException("Asset id is required");
        if (title == null || title.isBlank()) throw new IllegalArgumentException("Title is required");
        if (description == null || description.isBlank()) throw new IllegalArgumentException("Description is required");
        if (severity == null) throw new IllegalArgumentException("Severity is required");
        if (status == null) throw new IllegalArgumentException("Status is required");

        this.id = id;
        this.tenantId = tenantId;
        this.assetId = assetId;
        this.title = title.trim();
        this.description = description.trim();
        this.severity = severity;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static Incident report(
            UUID tenantId,
            UUID assetId,
            String title,
            String description,
            IncidentSeverity severity
    ) {
        return new Incident(
                UUID.randomUUID(),
                tenantId,
                assetId,
                title,
                description,
                severity,
                IncidentStatus.OPEN,
                LocalDateTime.now()
        );
    }

    public void updateStatus(IncidentStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("New status cannot be null");
        }
        this.status = newStatus;
    }

    public Incident update(
            String title,
            String description,
            IncidentSeverity severity,
            IncidentStatus status
    ) {
        return new Incident(
                id,
                tenantId,
                assetId,
                title,
                description,
                severity,
                status,
                createdAt
        );
    }

    public Incident close() {
        return update(title, description, severity, IncidentStatus.CLOSED);
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

    public IncidentSeverity getSeverity() {
        return severity;
    }

    public IncidentStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
