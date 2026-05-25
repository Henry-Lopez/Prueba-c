package com.aguafutura.platform.evidence.api;

import jakarta.servlet.http.HttpServletRequest;
import com.aguafutura.platform.assets.application.ListAssetsUseCase;
import com.aguafutura.platform.assets.domain.Asset;
import com.aguafutura.platform.evidence.application.ListEvidenceUseCase;
import com.aguafutura.platform.evidence.application.UploadEvidenceUseCase;
import com.aguafutura.platform.evidence.domain.Evidence;
import com.aguafutura.platform.evidence.domain.ReferenceType;
import com.aguafutura.platform.incidents.application.ListIncidentsUseCase;
import com.aguafutura.platform.incidents.domain.Incident;
import com.aguafutura.platform.workorders.application.ListWorkOrdersUseCase;
import com.aguafutura.platform.workorders.domain.WorkOrder;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/evidence")
public class EvidenceController {

    private final UploadEvidenceUseCase uploadEvidenceUseCase;
    private final ListEvidenceUseCase listEvidenceUseCase;
    private final ListAssetsUseCase listAssetsUseCase;
    private final ListIncidentsUseCase listIncidentsUseCase;
    private final ListWorkOrdersUseCase listWorkOrdersUseCase;

    public EvidenceController(
            UploadEvidenceUseCase uploadEvidenceUseCase,
            ListEvidenceUseCase listEvidenceUseCase,
            ListAssetsUseCase listAssetsUseCase,
            ListIncidentsUseCase listIncidentsUseCase,
            ListWorkOrdersUseCase listWorkOrdersUseCase
    ) {
        this.uploadEvidenceUseCase = uploadEvidenceUseCase;
        this.listEvidenceUseCase = listEvidenceUseCase;
        this.listAssetsUseCase = listAssetsUseCase;
        this.listIncidentsUseCase = listIncidentsUseCase;
        this.listWorkOrdersUseCase = listWorkOrdersUseCase;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EvidenceResponse> upload(
            @RequestParam("referenceType") ReferenceType referenceType,
            @RequestParam("referenceId") UUID referenceId,
            @RequestParam("file") MultipartFile file,
            Authentication authentication,
            HttpServletRequest servletRequest
    ) throws IOException {
        
        UUID tenantId = UUID.fromString(authentication.getDetails().toString());

        Evidence evidence = uploadEvidenceUseCase.execute(
                tenantId,
                actorId(authentication),
                actorRole(authentication),
                correlationId(servletRequest),
                referenceType,
                referenceId,
                file.getOriginalFilename(),
                file.getContentType(),
                file.getInputStream()
        );

        EvidenceResponse response = toResponse(evidence);

        return ResponseEntity
                .created(URI.create("/api/v1/evidence/" + response.id()))
                .body(response);
    }

    @GetMapping("/{referenceType}/{referenceId}")
    public ResponseEntity<List<EvidenceResponse>> list(
            @PathVariable ReferenceType referenceType,
            @PathVariable UUID referenceId,
            Authentication authentication
    ) {
        UUID tenantId = UUID.fromString(authentication.getDetails().toString());

        List<EvidenceResponse> evidences = listEvidenceUseCase.execute(tenantId, referenceType, referenceId)
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(evidences);
    }

    @GetMapping("/reference-options")
    public ResponseEntity<List<ReferenceOptionResponse>> referenceOptions(
            @RequestParam("type") ReferenceType type,
            Authentication authentication
    ) {
        UUID tenantId = UUID.fromString(authentication.getDetails().toString());

        return ResponseEntity.ok(switch (type) {
            case ASSET -> listAssetsUseCase.execute(tenantId)
                    .stream()
                    .map(this::assetReferenceOption)
                    .toList();
            case INCIDENT -> listIncidentsUseCase.execute(tenantId)
                    .stream()
                    .map(this::incidentReferenceOption)
                    .toList();
            case WORK_ORDER -> listWorkOrdersUseCase.execute(tenantId)
                    .stream()
                    .map(this::workOrderReferenceOption)
                    .toList();
        });
    }

    // Endpoint to download/view the file directly
    @GetMapping("/download/{tenantId}/{fileName}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable String tenantId,
            @PathVariable String fileName
    ) {
        try {
            Path filePath = Paths.get("uploads").resolve(tenantId).resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() || resource.isReadable()) {
                String contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private EvidenceResponse toResponse(Evidence evidence) {
        // En un caso real, esto sería una URL pre-firmada de S3.
        // Aquí construimos una URL que apunte a nuestro endpoint de descarga local.
        String fileName = evidence.getFilePath().substring(evidence.getFilePath().lastIndexOf("/") + 1);
        String url = "/api/v1/evidence/download/" + evidence.getTenantId() + "/" + fileName;

        return new EvidenceResponse(
                evidence.getId(),
                evidence.getReferenceType(),
                evidence.getReferenceId(),
                evidence.getFileName(),
                url,
                evidence.getCreatedAt()
        );
    }

    private ReferenceOptionResponse assetReferenceOption(Asset asset) {
        return new ReferenceOptionResponse(
                ReferenceType.ASSET,
                asset.getId(),
                asset.getCode() + " · " + asset.getName(),
                asset.getType().name()
        );
    }

    private ReferenceOptionResponse incidentReferenceOption(Incident incident) {
        return new ReferenceOptionResponse(
                ReferenceType.INCIDENT,
                incident.getId(),
                incident.getTitle(),
                incident.getStatus().name()
        );
    }

    private ReferenceOptionResponse workOrderReferenceOption(WorkOrder workOrder) {
        String shortId = workOrder.getId().toString().substring(0, 8).toUpperCase();
        return new ReferenceOptionResponse(
                ReferenceType.WORK_ORDER,
                workOrder.getId(),
                "OT-" + shortId + " · " + workOrder.getDescription(),
                workOrder.getStatus().name()
        );
    }

    private UUID actorId(Authentication authentication) {
        return UUID.fromString(authentication.getName());
    }

    private String actorRole(Authentication authentication) {
        return authentication.getAuthorities()
                .stream()
                .findFirst()
                .map(authority -> authority.getAuthority().replaceFirst("^ROLE_", ""))
                .orElse(null);
    }

    private String correlationId(HttpServletRequest request) {
        Object correlationId = request.getAttribute("correlationId");
        return correlationId != null ? correlationId.toString() : null;
    }
}
