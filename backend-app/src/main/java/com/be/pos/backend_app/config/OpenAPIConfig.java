package com.be.pos.backend_app.config;

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
        info = @Info(contact = @Contact(
                name = "dicka",
                email = "muhamad.nirwansyah@fwd.com"),
                description = "OpenApi documentation",
                title = "OpenApi specification - Backend Service",
                version = "1.0",
                license = @License(
                        name = "License name",
                        url = "https://medium.com/@dickanirwansyah"
                ),
                termsOfService = "Term of service"
        ),
        servers = {
                @Server(
                        description = "Local ENV",
                        url = "http://localhost:8888/"
                ),
                @Server(
                        description = "Prod ENV",
                        url = "https://dickanirwansyah.aws.co.id/api/v1"
                )
        },
        security = {
                @SecurityRequirement(
                        name = "bearerAuth"
                )
        }
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT auth description",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER)
public class OpenAPIConfig { }
