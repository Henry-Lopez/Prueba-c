package com.aguafutura.platform.iam.bootstrap;

import com.aguafutura.platform.core.application.port.AuditLogPort;
import com.aguafutura.platform.iam.application.LoginUseCase;
import com.aguafutura.platform.iam.application.CreateManagedUserUseCase;
import com.aguafutura.platform.iam.application.GetTechnicianWorkloadUseCase;
import com.aguafutura.platform.iam.application.GetManagedUserUseCase;
import com.aguafutura.platform.iam.application.ListUsersUseCase;
import com.aguafutura.platform.iam.application.RegisterUserUseCase;
import com.aguafutura.platform.iam.application.port.JwtTokenPort;
import com.aguafutura.platform.iam.application.port.PasswordHasherPort;
import com.aguafutura.platform.iam.application.port.UserRepositoryPort;
import com.aguafutura.platform.workorders.application.port.WorkOrderRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class IamApplicationConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RegisterUserUseCase registerUserUseCase(
            UserRepositoryPort userRepositoryPort,
            PasswordHasherPort passwordHasherPort,
            JwtTokenPort jwtTokenPort,
            AuditLogPort auditLogPort
    ) {
        return new RegisterUserUseCase(
                userRepositoryPort,
                passwordHasherPort,
                jwtTokenPort,
                auditLogPort
        );
    }

    @Bean
    public LoginUseCase loginUseCase(
            UserRepositoryPort userRepositoryPort,
            PasswordHasherPort passwordHasherPort,
            JwtTokenPort jwtTokenPort,
            AuditLogPort auditLogPort
    ) {
        return new LoginUseCase(
                userRepositoryPort,
                passwordHasherPort,
                jwtTokenPort,
                auditLogPort
        );
    }

    @Bean
    public CreateManagedUserUseCase createManagedUserUseCase(
            UserRepositoryPort userRepositoryPort,
            PasswordHasherPort passwordHasherPort,
            AuditLogPort auditLogPort
    ) {
        return new CreateManagedUserUseCase(userRepositoryPort, passwordHasherPort, auditLogPort);
    }

    @Bean
    public ListUsersUseCase listUsersUseCase(UserRepositoryPort userRepositoryPort) {
        return new ListUsersUseCase(userRepositoryPort);
    }

    @Bean
    public GetManagedUserUseCase getManagedUserUseCase(UserRepositoryPort userRepositoryPort) {
        return new GetManagedUserUseCase(userRepositoryPort);
    }

    @Bean
    public GetTechnicianWorkloadUseCase getTechnicianWorkloadUseCase(
            UserRepositoryPort userRepositoryPort,
            WorkOrderRepositoryPort workOrderRepositoryPort
    ) {
        return new GetTechnicianWorkloadUseCase(userRepositoryPort, workOrderRepositoryPort);
    }
}
