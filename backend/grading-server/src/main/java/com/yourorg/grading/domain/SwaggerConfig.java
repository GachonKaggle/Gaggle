package com.yourorg.grading.domain;

import io.swagger.v3.oas.models.OpenAPI; // Represents the OpenAPI main configuration object
import io.swagger.v3.oas.models.info.Info; // Contains API metadata like title, version, etc.
import io.swagger.v3.oas.models.security.SecurityRequirement; // Represents security requirement for API
import io.swagger.v3.oas.models.security.SecurityScheme; // Defines security scheme such as JWT bearer
import org.springframework.context.annotation.Bean; // Marks method as Spring Bean provider
import org.springframework.context.annotation.Configuration; // Marks this class as a configuration class

@Configuration // Indicates that this class contains Spring bean definitions
public class SwaggerConfig {

    @Bean // Declares a bean to be managed by the Spring container
    public OpenAPI customOpenAPI() {
        // Define the security scheme for JWT authentication
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP) // Specify HTTP type
                .scheme("bearer") // Use Bearer scheme for token-based authentication
                .bearerFormat("JWT") // Specify that the token format is JWT
                .in(SecurityScheme.In.HEADER) // Token is passed via HTTP header
                .name("Authorization"); // Header name expected to contain the token

        // Define the security requirement that references the scheme
        SecurityRequirement securityRequirement = new SecurityRequirement().addList("bearerAuth");

        // Create and return the OpenAPI specification with info and security settings
        return new OpenAPI()
                .info(new Info().title("Todolist API") // Set the title of the API documentation
                        .description("Todolist Application API Documentation") // Set the API description
                        .version("v1.0")) // Set the API version
                .addSecurityItem(securityRequirement) // Add security requirement globally
                .schemaRequirement("bearerAuth", securityScheme); // Register the defined security scheme
    }
}
