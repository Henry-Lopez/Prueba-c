package com.aguafutura.platform.incidents.bootstrap;

import com.aguafutura.platform.assets.application.port.AssetRepositoryPort;
import com.aguafutura.platform.core.application.port.AuditLogPort;
import com.aguafutura.platform.incidents.application.CloseIncidentUseCase;
import com.aguafutura.platform.incidents.application.GetIncidentUseCase;
import com.aguafutura.platform.incidents.application.ListIncidentsUseCase;
import com.aguafutura.platform.incidents.application.ReportIncidentUseCase;
import com.aguafutura.platform.incidents.application.UpdateIncidentUseCase;
import com.aguafutura.platform.incidents.application.port.IncidentRepositoryPort;
import com.aguafutura.platform.incidents.infrastructure.persistence.jpa.IncidentJpaRepository;
import com.aguafutura.platform.incidents.infrastructure.persistence.jpa.IncidentPersistenceAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IncidentsApplicationConfig {

    @Bean
    public IncidentRepositoryPort incidentRepositoryPort(IncidentJpaRepository repository) {
        return new IncidentPersistenceAdapter(repository);
    }

    @Bean
    public ReportIncidentUseCase reportIncidentUseCase(
            IncidentRepositoryPort port,
            AuditLogPort auditLogPort,
            AssetRepositoryPort assetRepositoryPort
    ) {
        return new ReportIncidentUseCase(port, auditLogPort, assetRepositoryPort);
    }

    @Bean
    public ListIncidentsUseCase listIncidentsUseCase(IncidentRepositoryPort port) {
        return new ListIncidentsUseCase(port);
    }

    @Bean
    public GetIncidentUseCase getIncidentUseCase(IncidentRepositoryPort port) {
        return new GetIncidentUseCase(port);
    }

    @Bean
    public UpdateIncidentUseCase updateIncidentUseCase(IncidentRepositoryPort port, AuditLogPort auditLogPort) {
        return new UpdateIncidentUseCase(port, auditLogPort);
    }

    @Bean
    public CloseIncidentUseCase closeIncidentUseCase(IncidentRepositoryPort port, AuditLogPort auditLogPort) {
        return new CloseIncidentUseCase(port, auditLogPort);
    }
}
