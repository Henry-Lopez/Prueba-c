package com.aguafutura.platform.assets.bootstrap;

import com.aguafutura.platform.assets.application.DisableAssetUseCase;
import com.aguafutura.platform.assets.application.CreateAssetUseCase;
import com.aguafutura.platform.assets.application.ListAssetsUseCase;
import com.aguafutura.platform.assets.application.UpdateAssetUseCase;
import com.aguafutura.platform.assets.application.port.AssetRepositoryPort;
import com.aguafutura.platform.core.application.port.AuditLogPort;
import com.aguafutura.platform.assets.infrastructure.persistence.jpa.AssetJpaRepository;
import com.aguafutura.platform.assets.infrastructure.persistence.jpa.AssetPersistenceAdapter;
import com.aguafutura.platform.territorial.application.port.ZoneRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AssetsApplicationConfig {

    @Bean
    public AssetRepositoryPort assetRepositoryPort(AssetJpaRepository assetJpaRepository) {
        return new AssetPersistenceAdapter(assetJpaRepository);
    }

    @Bean
    public CreateAssetUseCase createAssetUseCase(AssetRepositoryPort assetRepositoryPort, ZoneRepositoryPort zoneRepositoryPort) {
        return new CreateAssetUseCase(assetRepositoryPort, zoneRepositoryPort);
    }

    @Bean
    public ListAssetsUseCase listAssetsUseCase(AssetRepositoryPort assetRepositoryPort) {
        return new ListAssetsUseCase(assetRepositoryPort);
    }

    @Bean
    public UpdateAssetUseCase updateAssetUseCase(
            AssetRepositoryPort assetRepositoryPort,
            ZoneRepositoryPort zoneRepositoryPort,
            AuditLogPort auditLogPort
    ) {
        return new UpdateAssetUseCase(assetRepositoryPort, zoneRepositoryPort, auditLogPort);
    }

    @Bean
    public DisableAssetUseCase disableAssetUseCase(
            AssetRepositoryPort assetRepositoryPort,
            AuditLogPort auditLogPort
    ) {
        return new DisableAssetUseCase(assetRepositoryPort, auditLogPort);
    }
}
