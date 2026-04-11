package com.placeiq.api;

import com.placeiq.dto.*;
import com.placeiq.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Register, login and update profile")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @SecurityRequirements  // no token needed
    @Operation(summary = "Register a new user", description = "Creates either a student or admin account. Returns JWT token.")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.ok(ApiResponse.ok("Registration successful", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/login")
    @SecurityRequirements  // no token needed
    @Operation(summary = "Login", description = "Authenticate with email + password. Use the returned `token` in the Authorize dialog above.")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(ApiResponse.ok("Login successful", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/profile")
    @Operation(summary = "Update profile", description = "Update the currently logged-in student's profile fields.")
    public ResponseEntity<ApiResponse<AuthResponse.UserDto>> updateProfile(
            @RequestBody UpdateProfileRequest request,
            HttpServletRequest httpRequest) {
        try {
            String userId = (String) httpRequest.getAttribute("userId");
            if (userId == null) return ResponseEntity.status(401).body(ApiResponse.error("Unauthorized"));
            AuthResponse.UserDto updated = authService.updateProfile(userId, request);
            return ResponseEntity.ok(ApiResponse.ok("Profile updated", updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
