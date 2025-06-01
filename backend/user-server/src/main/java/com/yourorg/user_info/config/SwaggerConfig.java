package com.yourorg.user_info.config; // Define the package for configuration classes

import io.swagger.v3.oas.models.OpenAPI; // Import OpenAPI base class
import io.swagger.v3.oas.models.info.Info; // Import Info class for API metadata
import io.swagger.v3.oas.models.security.SecurityRequirement; // Import SecurityRequirement for securing endpoints
import io.swagger.v3.oas.models.security.SecurityScheme; // Import SecurityScheme for security configuration
import org.springframework.context.annotation.Bean; // Import Bean annotation to define a Spring bean
import org.springframework.context.annotation.Configuration; // Import Configuration annotation to mark this as a config class

@Configuration // Mark this class as a Spring configuration class
public class SwaggerConfig {

    @Bean // Define a Spring bean that returns an OpenAPI object
    public OpenAPI customOpenAPI() {
        // Define a security scheme using HTTP Bearer authentication (JWT)
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP) // Set the type to HTTP
                .scheme("bearer") // Set the scheme to "bearer"
                .bearerFormat("JWT") // Specify the format as JWT
                .in(SecurityScheme.In.HEADER) // Indicate the token is passed in the HTTP header
                .name("Authorization"); // Set the name of the header

        // Define a security requirement that uses the defined bearer authentication
        SecurityRequirement securityRequirement = new SecurityRequirement().addList("bearerAuth");

        // Create and return the OpenAPI configuration
        return new OpenAPI()
                .info(new Info().title("Todolist API") // Set the API title
                        .description("Todolist Application API Documentation") // Set the API description
                        .version("v1.0")) // Set the version
                .addSecurityItem(securityRequirement) // Add the security requirement to the API
                .schemaRequirement("bearerAuth", securityScheme); // Register the security scheme with the name "bearerAuth"
    }
}
