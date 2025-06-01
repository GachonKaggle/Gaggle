package com.yourorg.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component  // Marks this class as a Spring Bean so it can be auto-detected
public class JwtAuthorizationFilter implements GlobalFilter, Ordered {

    // Inject the JWT secret key from application properties or default to "mysecret"
    @Value("${jwt.secret:mysecret}")
    private String jwtSecret;

    // Define paths that should be excluded from JWT authentication
    private static final String[] IGNORE_PATHS = {
        "/api/public/",          // Public API
        "/api/user-info/login",  // Login endpoint
        "/api/user-info/signup", // Signup endpoint
        "/health",               // Health check endpoint
        "/actuator/health",      // Spring Actuator health check
        "/webjars/swagger-ui/index.html", // Swagger UI static path
        "/swagger-ui/index.html",         // Swagger UI main page
    };

    // Check if the current request path should bypass authentication
    private boolean isIgnored(String path) {
        for (String prefix : IGNORE_PATHS) {
            if (path.equals(prefix) || path.startsWith(prefix + "/")) return true;
        }
        return false;
    }

    // Main filtering logic that intercepts all HTTP requests
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // Skip authentication if path is in ignore list
        if (isIgnored(path)) {
            return chain.filter(exchange);
        }

        // Extract Authorization header
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        System.out.println("Authorization Header: " + authHeader);

        // Check if header is present and starts with Bearer token
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.replace("Bearer ", ""); // Remove "Bearer " prefix

            try {
                // Parse and validate the JWT token
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes())) // Set signing key
                        .build()
                        .parseClaimsJws(token)  // Parse the JWT
                        .getBody();             // Extract claims

                System.out.println("JWT Valid. Subject: " + claims.getSubject());

                // Continue to the next filter if token is valid
                return chain.filter(exchange);
            } catch (Exception e) {
                // JWT is invalid or expired
                e.printStackTrace();
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED); // Return 401 Unauthorized
                return exchange.getResponse().setComplete();
            }
        }

        // Authorization header is missing or not valid
        System.out.println("No Authorization header found");
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED); // Return 401 Unauthorized
        return exchange.getResponse().setComplete();
    }

    // Set filter order: a lower number means higher priority
    @Override
    public int getOrder() {
        return -1;  // High priority (runs early in filter chain)
    }
}
