package com.yourorg.gateway.filter;

import lombok.Data;                            // Lombok annotation to generate getters, setters, toString, etc.
import lombok.extern.slf4j.Slf4j;              // Lombok annotation to enable SLF4J logging
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j  // Enables SLF4J logging (e.g., log.info(...))
@Component  // Registers this class as a Spring Bean
public class LoggingFilter extends AbstractGatewayFilterFactory<LoggingFilter.Config> {

    // Constructor specifying the configuration class type
    public LoggingFilter() {
        super(Config.class);
    }

    // Main logic for the filter, applied when filter is configured in routes
    @Override
    public GatewayFilter apply(Config config) {
        // Define filter behavior: pre and post logic around request processing
        return new OrderedGatewayFilter((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();     // Get the incoming request
            ServerHttpResponse response = exchange.getResponse();  // Get the outgoing response

            // Log base message from config
            log.info("Logging Filter baseMessage: {}", config.getBaseMessage());

            // Log before the request is routed
            if (config.isPreLogger()) {
                log.info("Logging PRE Filter Start: request id -> {}", request.getId());
            }

            // Proceed with the filter chain, then execute post-processing logic
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                // Log after the response is completed
                if (config.isPostLogger()) {
                    log.info("Logging POST Filter End: response code -> {}", response.getStatusCode());
                }
            }));
        }, Ordered.LOWEST_PRECEDENCE);  // Set filter execution order (runs last)
    }

    // Configuration class for the LoggingFilter
    @Data  // Lombok will generate getter/setter/equals/hashCode/toString
    public static class Config {
        private String baseMessage;     // Custom message to log
        private boolean preLogger;      // Enable pre-request logging
        private boolean postLogger;     // Enable post-response logging
    }
}
