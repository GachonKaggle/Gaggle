package com.yourorg.gateway.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Marks this class as a Spring configuration class
@Configuration
public class SwaggerConfig {

    // Defines a Bean to customize OpenAPI documentation for Swagger UI
    @Bean
    public OpenAPI customOpenAPI() {
        // Define a security scheme for Bearer token (JWT-based authentication)
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)         // Use HTTP authentication type
                .scheme("bearer")                       // Specify Bearer scheme
                .bearerFormat("JWT")                    // Indicate the token format is JWT
                .in(SecurityScheme.In.HEADER)           // The token should be in the HTTP header
                .name("Authorization");                 // Header name is "Authorization"

        // Define a security requirement using the scheme name
        SecurityRequirement securityRequirement = new SecurityRequirement().addList("BearerAuth");

        // Create and return the OpenAPI instance with metadata and security configurations
        return new OpenAPI()
                .info(new Info()
                        .title("Todolist API")                       // API title shown in Swagger UI
                        .description("Todolist Application API Documentation") // Description
                        .version("v1.0"))                            // API version
                .addSecurityItem(securityRequirement)               // Apply security requirement globally
                .schemaRequirement("BearerAuth", securityScheme);   // Register the security scheme
    }
}
