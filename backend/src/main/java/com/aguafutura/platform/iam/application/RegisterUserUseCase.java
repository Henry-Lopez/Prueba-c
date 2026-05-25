package com.aguafutura.platform.iam.application;

import com.aguafutura.platform.core.application.port.AuditLogPort;
import com.aguafutura.platform.core.domain.AuditLog;
import com.aguafutura.platform.iam.application.port.JwtTokenPort;
import com.aguafutura.platform.iam.application.port.PasswordHasherPort;
import com.aguafutura.platform.iam.application.port.UserRepositoryPort;
import com.aguafutura.platform.iam.domain.User;
import com.aguafutura.platform.iam.domain.UserRole;

import java.util.UUID;

public class RegisterUserUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordHasherPort passwordHasher;
    private final JwtTokenPort jwtTokenPort;
    private final AuditLogPort auditLogPort;

    public RegisterUserUseCase(
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

    public String execute(UUID tenantId, String fullName, String email, String rawPassword, UserRole role, String correlationId) {
        if (userRepository.existsByTenantIdAndEmail(tenantId, email)) {
            throw new IllegalArgumentException("User already exists in this tenant");
        }

        String passwordHash = passwordHasher.hash(rawPassword);

        User user = User.create(
                tenantId,
                fullName,
                email,
                passwordHash,
                role
        );

        User savedUser = userRepository.save(user);

        auditLogPort.save(AuditLog.create(
                tenantId,
                savedUser.getId(),
                savedUser.getRole().name(),
                "USER_REGISTERED",
                "USER",
                savedUser.getId().toString(),
                correlationId
        ));

        return jwtTokenPort.generateAccessToken(savedUser);
    }
}
