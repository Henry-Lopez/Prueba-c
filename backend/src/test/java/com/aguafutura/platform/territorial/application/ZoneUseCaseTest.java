package com.aguafutura.platform.territorial.application;

import com.aguafutura.platform.core.application.ConflictException;
import com.aguafutura.platform.core.application.ResourceNotFoundException;
import com.aguafutura.platform.core.application.port.AuditLogPort;
import com.aguafutura.platform.core.domain.AuditLog;
import com.aguafutura.platform.territorial.application.port.ZoneRepositoryPort;
import com.aguafutura.platform.territorial.domain.Zone;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ZoneUseCaseTest {

    private final InMemoryZoneRepository repository = new InMemoryZoneRepository();
    private final AuditLogPort auditLogPort = auditLog -> auditLog;

    @Test
    void listReturnsOnlyEnabledZonesForTenant() {
        UUID tenantId = UUID.randomUUID();
        UUID otherTenantId = UUID.randomUUID();
        Zone enabled = repository.save(Zone.create(tenantId, "ZN-NORTE", "Zona Norte", null));
        repository.save(Zone.create(otherTenantId, "ZN-SUR", "Zona Sur", null));
        repository.save(enabled.disable());

        List<Zone> zones = new ListZonesUseCase(repository).execute(tenantId);

        assertThat(zones).isEmpty();
    }

    @Test
    void updateChangesOnlyZoneOwnedByTenant() {
        UUID tenantId = UUID.randomUUID();
        UUID otherTenantId = UUID.randomUUID();
        Zone zone = repository.save(Zone.create(otherTenantId, "ZN-NORTE", "Zona Norte", null));

        UpdateZoneUseCase useCase = new UpdateZoneUseCase(repository, auditLogPort);

        assertThatThrownBy(() -> useCase.execute(
                tenantId,
                UUID.randomUUID(),
                "ADMIN",
                "correlation-id",
                zone.getId(),
                "ZN-CENTRO",
                "Zona Centro",
                "Centro operativo",
                true
        )).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateRejectsDuplicatedCodeWithinTenant() {
        UUID tenantId = UUID.randomUUID();
        repository.save(Zone.create(tenantId, "ZN-NORTE", "Zona Norte", null));
        Zone zone = repository.save(Zone.create(tenantId, "ZN-SUR", "Zona Sur", null));

        UpdateZoneUseCase useCase = new UpdateZoneUseCase(repository, auditLogPort);

        assertThatThrownBy(() -> useCase.execute(
                tenantId,
                UUID.randomUUID(),
                "COORDINATOR",
                "correlation-id",
                zone.getId(),
                "ZN-NORTE",
                "Zona Norte Duplicada",
                null,
                true
        )).isInstanceOf(ConflictException.class);
    }

    @Test
    void disableUsesSoftDelete() {
        UUID tenantId = UUID.randomUUID();
        Zone zone = repository.save(Zone.create(tenantId, "ZN-NORTE", "Zona Norte", null));

        new DisableZoneUseCase(repository, auditLogPort)
                .execute(tenantId, UUID.randomUUID(), "ADMIN", "correlation-id", zone.getId());

        Zone disabled = repository.findByTenantIdAndId(tenantId, zone.getId()).orElseThrow();
        assertThat(disabled.getEnabled()).isFalse();
        assertThat(repository.findEnabledByTenantId(tenantId)).isEmpty();
    }

    @Test
    void invalidCodeIsRejected() {
        assertThatThrownBy(() -> Zone.create(UUID.randomUUID(), "ZN NORTE", "Zona Norte", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El código de zona debe ser corto y legible, por ejemplo ZN-NORTE o ZN-CENTRO.");
    }

    private static class InMemoryZoneRepository implements ZoneRepositoryPort {
        private final List<Zone> zones = new ArrayList<>();

        @Override
        public Zone save(Zone zone) {
            zones.removeIf(existing -> existing.getId().equals(zone.getId()));
            zones.add(zone);
            return zone;
        }

        @Override
        public List<Zone> findByTenantId(UUID tenantId) {
            return zones.stream()
                    .filter(zone -> zone.getTenantId().equals(tenantId))
                    .toList();
        }

        @Override
        public List<Zone> findEnabledByTenantId(UUID tenantId) {
            return zones.stream()
                    .filter(zone -> zone.getTenantId().equals(tenantId))
                    .filter(zone -> Boolean.TRUE.equals(zone.getEnabled()))
                    .toList();
        }

        @Override
        public Optional<Zone> findByTenantIdAndId(UUID tenantId, UUID id) {
            return zones.stream()
                    .filter(zone -> zone.getTenantId().equals(tenantId))
                    .filter(zone -> zone.getId().equals(id))
                    .findFirst();
        }

        @Override
        public boolean existsByTenantIdAndCode(UUID tenantId, String code) {
            return zones.stream()
                    .anyMatch(zone -> zone.getTenantId().equals(tenantId) && zone.getCode().equals(code));
        }

        @Override
        public boolean existsByTenantIdAndCodeAndIdNot(UUID tenantId, String code, UUID id) {
            return zones.stream()
                    .anyMatch(zone -> zone.getTenantId().equals(tenantId)
                            && zone.getCode().equals(code)
                            && !zone.getId().equals(id));
        }
    }
}
