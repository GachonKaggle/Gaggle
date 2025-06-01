package com.yourorg.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Marks this class as a configuration class for Spring
@Configuration
public class GatewayConfig {

    // Defines a bean that sets up custom routes for the API Gateway
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            // Route for the leaderboard service
            // All requests starting with /api/leaderboard/ will be forwarded to the LEADERBOARD-SERVER service
            .route("leaderboard-service", r -> r
                .path("/api/leaderboard/**")
                .uri("lb://LEADERBOARD-SERVER"))

            // Route for the grading service
            // All requests starting with /api/grading/ will be forwarded to the GRADING-SERVER service
            .route("grading-service", r -> r
                .path("/api/grading/**")
                .uri("lb://GRADING-SERVER"))

            // Route for the user info service
            // All requests starting with /api/user-info/ will be forwarded to the USER-INFO-SERVER service
            .route("user-info-service", r -> r
                .path("/api/user-info/**")
                .uri("lb://USER-INFO-SERVER"))

            // Finalize and build the routing configuration
            .build();
    }
}
