package com.aguafutura.platform.iam.application;

import com.aguafutura.platform.iam.application.port.UserRepositoryPort;
import com.aguafutura.platform.iam.domain.User;
import com.aguafutura.platform.iam.domain.UserRole;
import com.aguafutura.platform.workorders.application.port.WorkOrderRepositoryPort;
import com.aguafutura.platform.workorders.domain.WorkOrder;
import com.aguafutura.platform.workorders.domain.WorkOrderStatus;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class GetTechnicianWorkloadUseCase {

    public record TechnicianWorkload(
            User technician,
            long pendingOrders,
            long scheduledOrders,
            long inProgressOrders,
            long completedOrders,
            long cancelledOrders,
            long totalAssigned,
            LocalDateTime lastAssignedAt,
            String availability
    ) {}

    private final UserRepositoryPort userRepositoryPort;
    private final WorkOrderRepositoryPort workOrderRepositoryPort;

    public GetTechnicianWorkloadUseCase(
            UserRepositoryPort userRepositoryPort,
            WorkOrderRepositoryPort workOrderRepositoryPort
    ) {
        this.userRepositoryPort = userRepositoryPort;
        this.workOrderRepositoryPort = workOrderRepositoryPort;
    }

    public List<TechnicianWorkload> execute(UUID tenantId) {
        return userRepositoryPort.findAllByTenantIdAndRole(tenantId, UserRole.TECHNICIAN)
                .stream()
                .map(this::workloadFor)
                .toList();
    }

    private TechnicianWorkload workloadFor(User technician) {
        List<WorkOrder> orders = workOrderRepositoryPort.findAllByTenantIdAndAssignedTo(
                technician.getTenantId(),
                technician.getEmail()
        );
        long pending = count(orders, WorkOrderStatus.PENDING);
        long scheduled = count(orders, WorkOrderStatus.SCHEDULED);
        long inProgress = count(orders, WorkOrderStatus.IN_PROGRESS);
        long completed = count(orders, WorkOrderStatus.COMPLETED);
        long cancelled = count(orders, WorkOrderStatus.CANCELLED);
        long active = pending + scheduled + inProgress;
        String availability = active <= 2 ? "DISPONIBLE" : active <= 5 ? "OCUPADO" : "SATURADO";
        LocalDateTime lastAssignedAt = orders.stream()
                .map(WorkOrder::getCreatedAt)
                .max(Comparator.naturalOrder())
                .orElse(null);
        return new TechnicianWorkload(
                technician,
                pending,
                scheduled,
                inProgress,
                completed,
                cancelled,
                orders.size(),
                lastAssignedAt,
                availability
        );
    }

    private long count(List<WorkOrder> orders, WorkOrderStatus status) {
        return orders.stream().filter(order -> order.getStatus() == status).count();
    }
}
