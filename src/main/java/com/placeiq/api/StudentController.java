package com.placeiq.api;

import com.placeiq.dto.ApiResponse;
import com.placeiq.dto.AuthResponse;
import com.placeiq.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Tag(name = "Students", description = "Admin-only student management endpoints")
public class StudentController {

    private final StudentService studentService;

    /** GET /api/students — admin only */
    @Operation(summary = "List all students", description = "Admin only — returns all registered students.")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<AuthResponse.UserDto>>> getAllStudents() {
        return ResponseEntity.ok(ApiResponse.ok(studentService.getAllStudents()));
    }

    /** GET /api/students/{id} — admin only */
    @Operation(summary = "Get student by ID", description = "Admin only — returns a single student's profile.")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AuthResponse.UserDto>> getStudent(@PathVariable String id) {
        try {
            return ResponseEntity.ok(ApiResponse.ok(studentService.getStudentById(id)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
