package com.placeiq.api;

import com.placeiq.dto.ApiResponse;
import com.placeiq.model.Company;
import com.placeiq.service.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
@Tag(name = "Companies", description = "Browse and manage placement company drives")
public class CompanyController {

    private final CompanyService companyService;

    /** GET /api/companies — all authenticated users can see companies */
    @GetMapping
    @Operation(summary = "List companies", description = "Returns all companies. Filter by ?status=active or ?search=name")
    public ResponseEntity<ApiResponse<List<Company>>> getAllCompanies(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search) {
        List<Company> companies;
        if (search != null && !search.isBlank()) {
            companies = companyService.searchCompanies(search);
        } else if ("active".equals(status)) {
            companies = companyService.getActiveCompanies();
        } else {
            companies = companyService.getAllCompanies();
        }
        return ResponseEntity.ok(ApiResponse.ok(companies));
    }

    /** GET /api/companies/{id} */
    @GetMapping("/{id}")
    @Operation(summary = "Get company by ID")
    public ResponseEntity<ApiResponse<Company>> getCompany(@PathVariable String id) {
        try {
            return ResponseEntity.ok(ApiResponse.ok(companyService.getCompanyById(id)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /** POST /api/companies — admin only */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create company", description = "Admin only — add a new placement drive.")
    public ResponseEntity<ApiResponse<Company>> createCompany(@RequestBody Company company) {
        Company created = companyService.createCompany(company);
        return ResponseEntity.ok(ApiResponse.ok("Company created", created));
    }

    /** PUT /api/companies/{id} — admin only */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update company", description = "Admin only — update company fields.")
    public ResponseEntity<ApiResponse<Company>> updateCompany(@PathVariable String id,
                                                              @RequestBody Company company) {
        try {
            Company updated = companyService.updateCompany(id, company);
            return ResponseEntity.ok(ApiResponse.ok("Company updated", updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /** DELETE /api/companies/{id} — admin only */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete company", description = "Admin only — permanently remove a company.")
    public ResponseEntity<ApiResponse<Void>> deleteCompany(@PathVariable String id) {
        try {
            companyService.deleteCompany(id);
            return ResponseEntity.ok(ApiResponse.ok("Company deleted", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
