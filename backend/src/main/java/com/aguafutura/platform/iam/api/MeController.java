package com.aguafutura.platform.iam.api;

import com.aguafutura.platform.core.application.port.TenantRepositoryPort;
import com.aguafutura.platform.core.domain.Tenant;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class MeController {

    private final TenantRepositoryPort tenantRepositoryPort;

    public MeController(TenantRepositoryPort tenantRepositoryPort) {
        this.tenantRepositoryPort = tenantRepositoryPort;
    }

    @GetMapping("/api/v1/auth/me")
    public ResponseEntity<MeResponse> me(Authentication authentication) {
        String userId = authentication.getName();
        String tenantId = authentication.getDetails().toString();

        List<String> roles = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .map(authority -> authority.replaceFirst("^ROLE_", ""))
                .toList();
        Tenant tenant = tenantRepositoryPort.findById(UUID.fromString(tenantId)).orElse(null);

        return ResponseEntity.ok(
                new MeResponse(
                        userId,
                        tenantId,
                        roles,
                        tenantId.substring(0, Math.min(8, tenantId.length())),
                        tenant != null ? tenant.name() : null
                )
        );
    }
}
