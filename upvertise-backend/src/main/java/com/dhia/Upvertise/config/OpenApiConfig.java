package com.dhia.Upvertise.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
@OpenAPIDefinition(
        info = @Info(
                title = "Upvertice API Documentation",
                version = "1.0",
                description = "OpenAPI documentation for Upvertice - A personalized products advertisement platform",
                termsOfService = "https://upvertice.com/terms",
                contact = @Contact(
                        name = "Upvertice Support",
                        email = "meddhiarejeb22@gmail.com",
                        url = "https://upvertice.com"
                ),
                license = @License(
                        name = "Proprietary License",
                        url = "https://upvertice.com/license"
                )
        ),
        servers = {
                @Server(
                        description = "Local Environment",
                        url = "http://localhost:8088/api/v1"
                ),
                @Server(
                        description = "Production Environment",
                        url = "https://api.upvertice.com"
                )
        },
        security = {
                @SecurityRequirement(name = "bearerAuth")
        }
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "JWT Authentication using Keycloak",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
