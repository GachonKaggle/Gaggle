package com.yourorg.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("leaderboard-service", r -> r.path("/api/leaderboard/**").uri("lb://LEADERBOARD-SERVER"))
            .route("grading-service", r -> r.path("/api/grading/**").uri("lb://GRADING-SERVER"))
            .route("user-info-service", r -> r.path("/api/user-info/**").uri("lb://USER-INFO-SERVER"))
            .build();
    }
}
