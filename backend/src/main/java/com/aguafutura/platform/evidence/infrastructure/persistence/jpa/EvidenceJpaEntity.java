package com.aguafutura.platform.evidence.infrastructure.persistence.jpa;

import com.aguafutura.platform.evidence.domain.Evidence;
import com.aguafutura.platform.evidence.domain.EvidenceType;
import com.aguafutura.platform.evidence.domain.ReferenceType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "evidence")
public class EvidenceJpaEntity {

    @Id
    private UUID id;
    
    private UUID tenantId;
    private String referenceType;
    private UUID referenceId;
    private String evidenceType;
    
    private String fileName;
    private String contentType;
    private String filePath;
    
    private LocalDateTime createdAt;

    protected EvidenceJpaEntity() {}

    public static EvidenceJpaEntity fromDomain(Evidence domain) {
        EvidenceJpaEntity entity = new EvidenceJpaEntity();
        entity.id = domain.getId();
        entity.tenantId = domain.getTenantId();
        entity.referenceType = domain.getReferenceType().name();
        entity.referenceId = domain.getReferenceId();
        entity.evidenceType = domain.getEvidenceType().name();
        entity.fileName = domain.getFileName();
        entity.contentType = domain.getContentType();
        entity.filePath = domain.getFilePath();
        entity.createdAt = domain.getCreatedAt();
        return entity;
    }

    public Evidence toDomain() {
        return new Evidence(
                this.id,
                this.tenantId,
                ReferenceType.valueOf(this.referenceType),
                this.referenceId,
                this.evidenceType != null ? EvidenceType.valueOf(this.evidenceType) : EvidenceType.GENERAL_ATTACHMENT,
                this.fileName,
                this.contentType,
                this.filePath,
                this.createdAt
        );
    }
}
