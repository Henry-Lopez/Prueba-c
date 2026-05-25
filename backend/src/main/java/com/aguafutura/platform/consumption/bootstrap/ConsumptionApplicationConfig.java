package com.aguafutura.platform.consumption.bootstrap;

import com.aguafutura.platform.assets.application.port.AssetRepositoryPort;
import com.aguafutura.platform.consumption.application.ListConsumptionsUseCase;
import com.aguafutura.platform.consumption.application.RegisterConsumptionUseCase;
import com.aguafutura.platform.consumption.application.UpdateConsumptionUseCase;
import com.aguafutura.platform.consumption.application.port.ConsumptionRepositoryPort;
import com.aguafutura.platform.core.application.port.AuditLogPort;
import com.aguafutura.platform.consumption.infrastructure.persistence.jpa.ConsumptionJpaRepository;
import com.aguafutura.platform.consumption.infrastructure.persistence.jpa.ConsumptionPersistenceAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConsumptionApplicationConfig {

    @Bean
    public ConsumptionRepositoryPort consumptionRepositoryPort(ConsumptionJpaRepository repository) {
        return new ConsumptionPersistenceAdapter(repository);
    }

    @Bean
    public RegisterConsumptionUseCase registerConsumptionUseCase(ConsumptionRepositoryPort port) {
        return new RegisterConsumptionUseCase(port);
    }

    @Bean
    public ListConsumptionsUseCase listConsumptionsUseCase(ConsumptionRepositoryPort port) {
        return new ListConsumptionsUseCase(port);
    }

    @Bean
    public UpdateConsumptionUseCase updateConsumptionUseCase(
            ConsumptionRepositoryPort port,
            AssetRepositoryPort assetRepositoryPort,
            AuditLogPort auditLogPort
    ) {
        return new UpdateConsumptionUseCase(port, assetRepositoryPort, auditLogPort);
    }
}
