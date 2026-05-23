package com.aguafutura.platform.evidence.api;

import jakarta.servlet.http.HttpServletRequest;
import com.aguafutura.platform.evidence.application.ListEvidenceUseCase;
import com.aguafutura.platform.evidence.application.UploadEvidenceUseCase;
import com.aguafutura.platform.evidence.domain.Evidence;
import com.aguafutura.platform.evidence.domain.ReferenceType;
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

    public EvidenceController(
            UploadEvidenceUseCase uploadEvidenceUseCase,
            ListEvidenceUseCase listEvidenceUseCase
    ) {
        this.uploadEvidenceUseCase = uploadEvidenceUseCase;
        this.listEvidenceUseCase = listEvidenceUseCase;
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
            @PathVariable UUID referenceId
    ) {
        List<EvidenceResponse> evidences = listEvidenceUseCase.execute(referenceType, referenceId)
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(evidences);
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
