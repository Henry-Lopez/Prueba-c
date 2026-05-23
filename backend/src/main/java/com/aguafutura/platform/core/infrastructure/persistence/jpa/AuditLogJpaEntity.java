package com.aguafutura.platform.core.infrastructure.persistence.jpa;

import com.aguafutura.platform.core.domain.AuditLog;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "audit_log")
public class AuditLogJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id")
    private UUID tenantId;

    @Column(name = "actor_id")
    private UUID actorId;

    @Column(name = "actor_role")
    private String actorRole;

    private String action;

    @Column(name = "resource_type")
    private String resourceType;

    @Column(name = "resource_id")
    private String resourceId;

    @Column(name = "correlation_id")
    private String correlationId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    protected AuditLogJpaEntity() {
    }

    public static AuditLogJpaEntity fromDomain(AuditLog auditLog) {
        AuditLogJpaEntity entity = new AuditLogJpaEntity();
        entity.id = auditLog.getId();
        entity.tenantId = auditLog.getTenantId();
        entity.actorId = auditLog.getActorId();
        entity.actorRole = auditLog.getActorRole();
        entity.action = auditLog.getAction();
        entity.resourceType = auditLog.getResourceType();
        entity.resourceId = auditLog.getResourceId();
        entity.correlationId = auditLog.getCorrelationId();
        entity.createdAt = auditLog.getCreatedAt();
        return entity;
    }

    public AuditLog toDomain() {
        return new AuditLog(
                id,
                tenantId,
                actorId,
                actorRole,
                action,
                resourceType,
                resourceId,
                correlationId,
                createdAt
        );
    }
}
