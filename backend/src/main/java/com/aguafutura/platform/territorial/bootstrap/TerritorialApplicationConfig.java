package com.aguafutura.platform.territorial.bootstrap;

import com.aguafutura.platform.core.application.port.AuditLogPort;
import com.aguafutura.platform.territorial.application.CreateZoneUseCase;
import com.aguafutura.platform.territorial.application.DisableZoneUseCase;
import com.aguafutura.platform.territorial.application.ListZonesUseCase;
import com.aguafutura.platform.territorial.application.UpdateZoneUseCase;
import com.aguafutura.platform.territorial.application.port.ZoneRepositoryPort;
import com.aguafutura.platform.territorial.infrastructure.persistence.jpa.ZoneJpaRepository;
import com.aguafutura.platform.territorial.infrastructure.persistence.jpa.ZonePersistenceAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TerritorialApplicationConfig {

    @Bean
    public ZoneRepositoryPort zoneRepositoryPort(ZoneJpaRepository zoneJpaRepository) {
        return new ZonePersistenceAdapter(zoneJpaRepository);
    }

    @Bean
    public CreateZoneUseCase createZoneUseCase(ZoneRepositoryPort zoneRepositoryPort) {
        return new CreateZoneUseCase(zoneRepositoryPort);
    }

    @Bean
    public ListZonesUseCase listZonesUseCase(ZoneRepositoryPort zoneRepositoryPort) {
        return new ListZonesUseCase(zoneRepositoryPort);
    }

    @Bean
    public UpdateZoneUseCase updateZoneUseCase(ZoneRepositoryPort zoneRepositoryPort, AuditLogPort auditLogPort) {
        return new UpdateZoneUseCase(zoneRepositoryPort, auditLogPort);
    }

    @Bean
    public DisableZoneUseCase disableZoneUseCase(ZoneRepositoryPort zoneRepositoryPort, AuditLogPort auditLogPort) {
        return new DisableZoneUseCase(zoneRepositoryPort, auditLogPort);
    }
}
