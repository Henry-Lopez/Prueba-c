package com.aguafutura.platform.evidence.bootstrap;

import com.aguafutura.platform.core.application.port.AuditLogPort;
import com.aguafutura.platform.evidence.application.ListEvidenceUseCase;
import com.aguafutura.platform.evidence.application.UploadEvidenceUseCase;
import com.aguafutura.platform.evidence.application.port.EvidenceRepositoryPort;
import com.aguafutura.platform.evidence.application.port.EvidenceStoragePort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EvidenceApplicationConfig {

    @Bean
    public UploadEvidenceUseCase uploadEvidenceUseCase(
            EvidenceRepositoryPort repositoryPort,
            EvidenceStoragePort storagePort,
            AuditLogPort auditLogPort
    ) {
        return new UploadEvidenceUseCase(repositoryPort, storagePort, auditLogPort);
    }

    @Bean
    public ListEvidenceUseCase listEvidenceUseCase(EvidenceRepositoryPort repositoryPort) {
        return new ListEvidenceUseCase(repositoryPort);
    }
}
