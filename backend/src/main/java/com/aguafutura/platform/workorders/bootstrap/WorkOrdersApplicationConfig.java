package com.aguafutura.platform.workorders.bootstrap;

import com.aguafutura.platform.assets.application.port.AssetRepositoryPort;
import com.aguafutura.platform.incidents.application.port.IncidentRepositoryPort;
import com.aguafutura.platform.workorders.application.CreateWorkOrderUseCase;
import com.aguafutura.platform.workorders.application.ListWorkOrdersUseCase;
import com.aguafutura.platform.workorders.application.port.WorkOrderRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WorkOrdersApplicationConfig {

    @Bean
    public CreateWorkOrderUseCase createWorkOrderUseCase(
            WorkOrderRepositoryPort repositoryPort,
            AssetRepositoryPort assetRepositoryPort,
            IncidentRepositoryPort incidentRepositoryPort
    ) {
        return new CreateWorkOrderUseCase(repositoryPort, assetRepositoryPort, incidentRepositoryPort);
    }

    @Bean
    public ListWorkOrdersUseCase listWorkOrdersUseCase(WorkOrderRepositoryPort repositoryPort) {
        return new ListWorkOrdersUseCase(repositoryPort);
    }
}
