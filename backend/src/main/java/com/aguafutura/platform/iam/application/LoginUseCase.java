package com.aguafutura.platform.iam.application;

import com.aguafutura.platform.core.application.port.AuditLogPort;
import com.aguafutura.platform.core.domain.AuditLog;
import com.aguafutura.platform.iam.application.port.JwtTokenPort;
import com.aguafutura.platform.iam.application.port.PasswordHasherPort;
import com.aguafutura.platform.iam.application.port.UserRepositoryPort;
import com.aguafutura.platform.iam.domain.User;

import java.util.UUID;

public class LoginUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordHasherPort passwordHasher;
    private final JwtTokenPort jwtTokenPort;
    private final AuditLogPort auditLogPort;

    public LoginUseCase(
            UserRepositoryPort userRepository,
            PasswordHasherPort passwordHasher,
            JwtTokenPort jwtTokenPort,
            AuditLogPort auditLogPort
    ) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.jwtTokenPort = jwtTokenPort;
        this.auditLogPort = auditLogPort;
    }

    public String execute(UUID tenantId, String email, String rawPassword, String correlationId) {
        User user = userRepository.findByTenantIdAndEmail(tenantId, email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!user.isEnabled()) {
            throw new IllegalArgumentException("User is disabled");
        }

        if (!passwordHasher.matches(rawPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        auditLogPort.save(AuditLog.create(
                tenantId,
                user.getId(),
                user.getRole().name(),
                "LOGIN_SUCCESS",
                "USER",
                user.getId().toString(),
                correlationId
        ));

        return jwtTokenPort.generateAccessToken(user);
    }
}
