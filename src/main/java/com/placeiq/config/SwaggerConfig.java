package com.placeiq.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    private static final String BEARER_AUTH = "bearerAuth";

    @Bean
    public OpenAPI placeIqOpenAPI() {
        return new OpenAPI()
                // API metadata
                .info(new Info()
                        .title("PlaceIQ – College Placement Management System API")
                        .description("""
                                REST API for PlaceIQ, a full-stack college placement management platform.
                                
                                **Authentication:** All protected endpoints require a JWT Bearer token.
                                1. Call `POST /api/auth/login` with your credentials.
                                2. Copy the `token` from the response.
                                3. Click **Authorize** (lock icon) above and enter: `Bearer <your_token>`
                                
                                **Roles:**
                                - `student` – can browse companies, apply, and view their own applications
                                - `admin`   – full read/write access across companies, applications, students
                                """)
                        .version("2.0.0")
                        .contact(new Contact()
                                .name("PlaceIQ Dev Team")
                                .email("admin@pis.com")))
                // Default server
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local Development")))
                // JWT Bearer security scheme
                .components(new Components()
                        .addSecuritySchemes(BEARER_AUTH, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Paste your JWT token here (without 'Bearer ' prefix)")))
                // Apply globally — each endpoint can override with @SecurityRequirements
                .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH));
    }
}
