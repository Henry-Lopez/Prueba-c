package com.aguafutura.platform.core.api;

import com.aguafutura.platform.core.application.ListTenantsUseCase;
import com.aguafutura.platform.core.application.CreateTenantUseCase;
import com.aguafutura.platform.core.domain.Tenant;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.net.URI;
import java.util.UUID;

@RestController
public class TenantController {

    private final ListTenantsUseCase listTenantsUseCase;
    private final CreateTenantUseCase createTenantUseCase;
    private final com.aguafutura.platform.core.application.port.TenantRepositoryPort tenantRepositoryPort;

    public TenantController(
            ListTenantsUseCase listTenantsUseCase,
            CreateTenantUseCase createTenantUseCase,
            com.aguafutura.platform.core.application.port.TenantRepositoryPort tenantRepositoryPort
    ) {
        this.listTenantsUseCase = listTenantsUseCase;
        this.createTenantUseCase = createTenantUseCase;
        this.tenantRepositoryPort = tenantRepositoryPort;
    }

    @GetMapping("/api/v1/tenants")
    public List<TenantResponse> listTenants() {
        return listTenantsUseCase.execute()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @PostMapping("/api/v1/tenants")
    public ResponseEntity<TenantResponse> createTenant(@RequestBody CreateTenantRequest request) {
        Tenant tenant = createTenantUseCase.execute(request.code(), request.name());
        return ResponseEntity
                .created(URI.create("/api/v1/tenants/" + tenant.id()))
                .body(toResponse(tenant));
    }

    @GetMapping("/api/v1/tenants/{id}")
    public TenantResponse getTenant(@PathVariable UUID id) {
        return tenantRepositoryPort.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new com.aguafutura.platform.core.application.ResourceNotFoundException("Tenant not found"));
    }

    private TenantResponse toResponse(Tenant tenant) {
        return new TenantResponse(
                tenant.id(),
                tenant.code(),
                tenant.name(),
                tenant.status()
        );
    }
}
