package com.aguafutura.platform.core.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public class AuditLog {

    private final UUID id;
    private final UUID tenantId;
    private final UUID actorId;
    private final String actorRole;
    private final String action;
    private final String resourceType;
    private final String resourceId;
    private final String correlationId;
    private final LocalDateTime createdAt;

    public AuditLog(
            UUID id,
            UUID tenantId,
            UUID actorId,
            String actorRole,
            String action,
            String resourceType,
            String resourceId,
            String correlationId,
            LocalDateTime createdAt
    ) {
        if (id == null) throw new IllegalArgumentException("Audit id is required");
        if (action == null || action.isBlank()) throw new IllegalArgumentException("Audit action is required");
        if (resourceType == null || resourceType.isBlank()) throw new IllegalArgumentException("Audit resource type is required");

        this.id = id;
        this.tenantId = tenantId;
        this.actorId = actorId;
        this.actorRole = actorRole;
        this.action = action;
        this.resourceType = resourceType;
        this.resourceId = resourceId;
        this.correlationId = correlationId;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    public static AuditLog create(
            UUID tenantId,
            UUID actorId,
            String actorRole,
            String action,
            String resourceType,
            String resourceId,
            String correlationId
    ) {
        return new AuditLog(
                UUID.randomUUID(),
                tenantId,
                actorId,
                actorRole,
                action,
                resourceType,
                resourceId,
                correlationId,
                LocalDateTime.now()
        );
    }

    public UUID getId() { return id; }
    public UUID getTenantId() { return tenantId; }
    public UUID getActorId() { return actorId; }
    public String getActorRole() { return actorRole; }
    public String getAction() { return action; }
    public String getResourceType() { return resourceType; }
    public String getResourceId() { return resourceId; }
    public String getCorrelationId() { return correlationId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
