package com.aguafutura.platform.core.infrastructure.persistence.jpa;

import com.aguafutura.platform.core.domain.Tenant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tenant")
public class TenantJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 80)
    private String code;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 50)
    private String status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Version
    @Column(name = "version_lock", nullable = false)
    private Integer versionLock;

    protected TenantJpaEntity() {
    }

    public TenantJpaEntity(UUID id, String code, String name, String status) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.status = status;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.versionLock = 0;
    }

    public Tenant toDomain() {
        return new Tenant(
                id,
                code,
                name,
                status,
                createdAt,
                updatedAt,
                versionLock
        );
    }

    public static TenantJpaEntity fromDomain(Tenant tenant) {
        TenantJpaEntity entity = new TenantJpaEntity();
        entity.id = tenant.id();
        entity.code = tenant.code();
        entity.name = tenant.name();
        entity.status = tenant.status();
        entity.createdAt = tenant.createdAt() != null ? tenant.createdAt() : LocalDateTime.now();
        entity.updatedAt = tenant.updatedAt() != null ? tenant.updatedAt() : LocalDateTime.now();
        entity.versionLock = tenant.versionLock() != null ? tenant.versionLock() : 0;
        return entity;
    }
}
