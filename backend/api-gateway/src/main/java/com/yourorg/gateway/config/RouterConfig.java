package com.yourorg.gateway.config;

import com.yourorg.gateway.handler.FallbackHandler;
import com.yourorg.gateway.handler.HealthCheckHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class RouterConfig {

    @Bean
    public RouterFunction<ServerResponse> route(FallbackHandler fallbackHandler, HealthCheckHandler healthCheckHandler) {
        return RouterFunctions
                .route()
                .GET("/health", healthCheckHandler::healthCheck)
                .GET("/actuator/health", healthCheckHandler::healthCheck)
                .GET("/fallback", fallbackHandler::fallback)
                .POST("/fallback", fallbackHandler::fallback)
                .build();
    }
}
