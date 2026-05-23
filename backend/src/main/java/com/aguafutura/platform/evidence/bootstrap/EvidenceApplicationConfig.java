package com.aguafutura.platform.evidence.bootstrap;

import com.aguafutura.platform.assets.application.port.AssetRepositoryPort;
import com.aguafutura.platform.evidence.application.GetEvidenceUseCase;
import com.aguafutura.platform.evidence.application.ListEvidenceUseCase;
import com.aguafutura.platform.evidence.application.UploadEvidenceUseCase;
import com.aguafutura.platform.evidence.application.port.EvidenceRepositoryPort;
import com.aguafutura.platform.evidence.application.port.EvidenceStoragePort;
import com.aguafutura.platform.incidents.application.port.IncidentRepositoryPort;
import com.aguafutura.platform.workorders.application.port.WorkOrderRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EvidenceApplicationConfig {

    @Bean
    public UploadEvidenceUseCase uploadEvidenceUseCase(
            EvidenceRepositoryPort repositoryPort,
            EvidenceStoragePort storagePort,
            AssetRepositoryPort assetRepositoryPort,
            IncidentRepositoryPort incidentRepositoryPort,
            WorkOrderRepositoryPort workOrderRepositoryPort
    ) {
        return new UploadEvidenceUseCase(
                repositoryPort,
                storagePort,
                assetRepositoryPort,
                incidentRepositoryPort,
                workOrderRepositoryPort
        );
    }

    @Bean
    public ListEvidenceUseCase listEvidenceUseCase(EvidenceRepositoryPort repositoryPort) {
        return new ListEvidenceUseCase(repositoryPort);
    }

    @Bean
    public GetEvidenceUseCase getEvidenceUseCase(EvidenceRepositoryPort repositoryPort) {
        return new GetEvidenceUseCase(repositoryPort);
    }
}
