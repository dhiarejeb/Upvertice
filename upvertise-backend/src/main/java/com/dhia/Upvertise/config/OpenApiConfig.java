package com.dhia.Upvertise.config;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
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
        description = "JWT Authentication using Keycloak",
        scheme = "bearer",
        type = SecuritySchemeType.OAUTH2,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER,
        flows = @OAuthFlows(
                authorizationCode = @OAuthFlow(
                        authorizationUrl = "https://keycloak.upvertice.com/realms/upvertice/protocol/openid-connect/auth",
                        tokenUrl = "https://keycloak.upvertice.com/realms/upvertice/protocol/openid-connect/token"
                )
        )
)
public class OpenApiConfig {
}
