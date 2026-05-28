package com.aguafutura.platform.iam.api;

import com.aguafutura.platform.iam.application.CreateManagedUserUseCase;
import com.aguafutura.platform.iam.application.GetTechnicianWorkloadUseCase;
import com.aguafutura.platform.iam.application.GetManagedUserUseCase;
import com.aguafutura.platform.iam.application.ListUsersUseCase;
import com.aguafutura.platform.iam.domain.User;
import com.aguafutura.platform.iam.domain.UserRole;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserManagementController {

    private final ListUsersUseCase listUsersUseCase;
    private final CreateManagedUserUseCase createManagedUserUseCase;
    private final GetTechnicianWorkloadUseCase technicianWorkloadUseCase;
    private final GetManagedUserUseCase getManagedUserUseCase;

    public UserManagementController(
            ListUsersUseCase listUsersUseCase,
            CreateManagedUserUseCase createManagedUserUseCase,
            GetTechnicianWorkloadUseCase technicianWorkloadUseCase,
            GetManagedUserUseCase getManagedUserUseCase
    ) {
        this.listUsersUseCase = listUsersUseCase;
        this.createManagedUserUseCase = createManagedUserUseCase;
        this.technicianWorkloadUseCase = technicianWorkloadUseCase;
        this.getManagedUserUseCase = getManagedUserUseCase;
    }

    @GetMapping("/{userId}")
    public UserResponse get(
            @PathVariable UUID userId,
            @RequestParam(required = false) UUID tenantId,
            Authentication authentication
    ) {
        return toResponse(getManagedUserUseCase.execute(
                actorTenantId(authentication),
                actorRole(authentication),
                tenantId,
                userId
        ));
    }

    @GetMapping
    public List<UserResponse> list(@RequestParam(required = false) UUID tenantId, Authentication authentication) {
        return listUsersUseCase.execute(actorTenantId(authentication), actorRole(authentication), tenantId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(
            @RequestBody CreateUserRequest request,
            Authentication authentication,
            HttpServletRequest servletRequest
    ) {
        User user = createManagedUserUseCase.execute(
                actorTenantId(authentication),
                actorId(authentication),
                actorRole(authentication),
                correlationId(servletRequest),
                request.tenantId(),
                request.fullName(),
                request.email(),
                request.password(),
                request.role()
        );
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .location(URI.create("/api/v1/users/" + user.getId()))
                .body(toResponse(user));
    }

    @GetMapping("/technicians/workload")
    public List<TechnicianWorkloadResponse> technicianWorkload(
            @RequestParam(required = false) UUID tenantId,
            Authentication authentication
    ) {
        UUID targetTenantId = actorRole(authentication) == UserRole.SUPER_ADMIN
                ? tenantId
                : actorTenantId(authentication);
        if (targetTenantId == null) {
            throw new IllegalArgumentException("tenantId query parameter is required for SUPER_ADMIN");
        }
        return technicianWorkloadUseCase.execute(targetTenantId)
                .stream()
                .map(workload -> new TechnicianWorkloadResponse(
                        workload.technician().getId(),
                        workload.technician().getFullName(),
                        workload.technician().getEmail(),
                        workload.pendingOrders(),
                        workload.scheduledOrders(),
                        workload.inProgressOrders(),
                        workload.completedOrders(),
                        workload.cancelledOrders(),
                        workload.totalAssigned(),
                        workload.lastAssignedAt(),
                        workload.availability()
                ))
                .toList();
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getTenantId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.isEnabled()
        );
    }

    private UUID actorId(Authentication authentication) {
        return UUID.fromString(authentication.getName());
    }

    private UUID actorTenantId(Authentication authentication) {
        return UUID.fromString(authentication.getDetails().toString());
    }

    private UserRole actorRole(Authentication authentication) {
        return authentication.getAuthorities()
                .stream()
                .findFirst()
                .map(authority -> authority.getAuthority().replaceFirst("^ROLE_", ""))
                .map(UserRole::valueOf)
                .orElse(null);
    }

    private String correlationId(HttpServletRequest request) {
        Object correlationId = request.getAttribute("correlationId");
        return correlationId != null ? correlationId.toString() : null;
    }
}
