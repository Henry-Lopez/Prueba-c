package com.aguafutura.platform.workorders.bootstrap;

import com.aguafutura.platform.core.application.port.AuditLogPort;
import com.aguafutura.platform.workorders.application.CreateWorkOrderUseCase;
import com.aguafutura.platform.workorders.application.ListWorkOrdersUseCase;
import com.aguafutura.platform.workorders.application.port.WorkOrderRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WorkOrdersApplicationConfig {

    @Bean
    public CreateWorkOrderUseCase createWorkOrderUseCase(WorkOrderRepositoryPort repositoryPort, AuditLogPort auditLogPort) {
        return new CreateWorkOrderUseCase(repositoryPort, auditLogPort);
    }

    @Bean
    public ListWorkOrdersUseCase listWorkOrdersUseCase(WorkOrderRepositoryPort repositoryPort) {
        return new ListWorkOrdersUseCase(repositoryPort);
    }
}
