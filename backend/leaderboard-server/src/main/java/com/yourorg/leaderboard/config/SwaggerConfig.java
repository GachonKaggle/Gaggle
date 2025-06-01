package com.yourorg.leaderboard.config; // Package declaration for configuration classes

import io.swagger.v3.oas.models.OpenAPI; // OpenAPI root model
import io.swagger.v3.oas.models.info.Info; // Class for API metadata (title, version, description)
import io.swagger.v3.oas.models.security.SecurityRequirement; // Class for declaring security requirements
import io.swagger.v3.oas.models.security.SecurityScheme; // Class for defining a security scheme
import org.springframework.context.annotation.Bean; // Annotation to define a Spring Bean
import org.springframework.context.annotation.Configuration; // Annotation to declare a configuration class

@Configuration // Marks this class as a Spring configuration class
public class SwaggerConfig {

    @Bean // Declares this method as a Spring Bean
    public OpenAPI customOpenAPI() {
        // Define the security scheme (Bearer JWT in header)
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP) // Set type as HTTP authentication
                .scheme("bearer") // Set scheme as bearer token
                .bearerFormat("JWT") // Specify the format as JWT
                .in(SecurityScheme.In.HEADER) // Indicate the token will be sent in the header
                .name("Authorization"); // Header name for the token

        // Define the security requirement that uses the above scheme
        SecurityRequirement securityRequirement = new SecurityRequirement().addList("bearerAuth");

        // Return the customized OpenAPI object
        return new OpenAPI()
                .info(new Info().title("Todolist API") // Set API title
                        .description("Todolist Application API Documentation") // Set API description
                        .version("v1.0")) // Set API version
                .addSecurityItem(securityRequirement) // Add security requirement to the API
                .schemaRequirement("bearerAuth", securityScheme); // Register the security scheme
    }
}
