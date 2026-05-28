package com.aguafutura.platform.core.bootstrap;

import com.aguafutura.platform.core.application.ListTenantsUseCase;
import com.aguafutura.platform.core.application.CreateTenantUseCase;
import com.aguafutura.platform.core.application.port.TenantRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CoreApplicationConfig {

    @Bean
    public ListTenantsUseCase listTenantsUseCase(TenantRepositoryPort tenantRepositoryPort) {
        return new ListTenantsUseCase(tenantRepositoryPort);
    }

    @Bean
    public CreateTenantUseCase createTenantUseCase(TenantRepositoryPort tenantRepositoryPort) {
        return new CreateTenantUseCase(tenantRepositoryPort);
    }
}
