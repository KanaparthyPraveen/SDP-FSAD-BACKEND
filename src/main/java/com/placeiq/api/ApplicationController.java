package com.placeiq.api;

import com.placeiq.dto.AdminUpdateApplicationRequest;
import com.placeiq.dto.ApiResponse;
import com.placeiq.dto.ApplyRequest;
import com.placeiq.model.Application;
import com.placeiq.service.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
@Tag(name = "Applications", description = "Student apply flow and admin round management")
public class ApplicationController {

    private final ApplicationService applicationService;

    /**
     * GET /api/applications
     * - Admin: returns ALL applications
     * - Student: returns only their own
     */
    @GetMapping
    @Operation(summary = "Get applications", description = "Admin receives ALL applications; student receives only their own.")
    public ResponseEntity<ApiResponse<List<Application>>> getApplications(HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        String role   = (String) request.getAttribute("userRole");

        if (userId == null) return ResponseEntity.status(401).body(ApiResponse.error("Unauthorized"));

        List<Application> apps = "admin".equals(role)
                ? applicationService.getAllApplications()
                : applicationService.getMyApplications(userId);

        return ResponseEntity.ok(ApiResponse.ok(apps));
    }

    /**
     * POST /api/applications
     * Student applies to a company
     */
    @PostMapping
    @Operation(summary = "Apply to a company", description = "Student applies. Creates a multi-round application record. Validates CGPA, deadline, and slot availability.")
    public ResponseEntity<ApiResponse<Application>> apply(@RequestBody ApplyRequest request,
                                                          HttpServletRequest httpRequest) {
        try {
            String userId = (String) httpRequest.getAttribute("userId");
            if (userId == null) return ResponseEntity.status(401).body(ApiResponse.error("Unauthorized"));
            Application app = applicationService.apply(userId, request);
            return ResponseEntity.ok(ApiResponse.ok("Applied successfully", app));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * PUT /api/applications/{id}
     * Admin: update round status, override status, add notes
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update application (admin)", description = "Update a round status (roundIndex + roundStatus) or override final status. Auto-derives status from rounds.")
    public ResponseEntity<ApiResponse<Application>> adminUpdate(@PathVariable String id,
                                                                @RequestBody AdminUpdateApplicationRequest req) {
        try {
            Application updated = applicationService.adminUpdate(id, req);
            return ResponseEntity.ok(ApiResponse.ok("Application updated", updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * GET /api/applications/company/{companyId}
     * Admin: get all applications for a specific company
     */
    @GetMapping("/company/{companyId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get applications by company", description = "Admin only — returns all applicants for a specific company.")
    public ResponseEntity<ApiResponse<List<Application>>> getByCompany(@PathVariable String companyId) {
        return ResponseEntity.ok(ApiResponse.ok(applicationService.getApplicationsByCompany(companyId)));
    }
}
