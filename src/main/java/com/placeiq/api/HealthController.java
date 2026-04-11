package com.placeiq.api;

import com.placeiq.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * HealthController — a public endpoint to verify the backend is running.
 * Test it at: GET http://localhost:8080/api/health
 */
@RestController
@RequestMapping("/api/health")
@Tag(name = "Health", description = "Public health-check endpoint")
public class HealthController {

    @GetMapping
    @SecurityRequirements
    @Operation(summary = "Health check", description = "Returns backend status, timestamp, and database info. No authentication required.")
    public ResponseEntity<ApiResponse<Map<String, Object>>> health() {
        return ResponseEntity.ok(ApiResponse.ok("Backend is running", Map.of(
                "status", "UP",
                "timestamp", LocalDateTime.now().toString(),
                "version", "1.0.0",
                "database", "MongoDB"
        )));
    }
}
