package com.yourorg.gateway.config;

import com.yourorg.gateway.handler.FallbackHandler;          // Custom handler for fallback endpoints
import com.yourorg.gateway.handler.HealthCheckHandler;       // Custom handler for health check endpoints
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

// Marks this class as a configuration class for Spring Boot
@Configuration
public class RouterConfig {

    // Defines a RouterFunction bean that maps specific HTTP routes to handler methods
    @Bean
    public RouterFunction<ServerResponse> route(FallbackHandler fallbackHandler, HealthCheckHandler healthCheckHandler) {
        return RouterFunctions
                .route()
                // Route for GET request to "/health", handled by healthCheckHandler
                .GET("/health", healthCheckHandler::healthCheck)

                // Route for GET request to "/actuator/health", also handled by healthCheckHandler
                .GET("/actuator/health", healthCheckHandler::healthCheck)

                // Route for GET request to "/fallback", handled by fallbackHandler
                .GET("/fallback", fallbackHandler::fallback)

                // Route for POST request to "/fallback", handled by fallbackHandler
                .POST("/fallback", fallbackHandler::fallback)

                // Builds and returns the routing configuration
                .build();
    }
}
