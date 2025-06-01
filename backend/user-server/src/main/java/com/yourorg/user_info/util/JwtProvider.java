package com.yourorg.user_info.util; // Package declaration for utility classes

import io.jsonwebtoken.Jwts; // JWT utility for token creation
import io.jsonwebtoken.security.Keys; // Utility for secure key generation
import org.springframework.beans.factory.annotation.Value; // Annotation to inject property values
import org.springframework.stereotype.Component; // Marks this class as a Spring component
import java.nio.charset.StandardCharsets; // Charset specification for key encoding
import java.util.Date; // Date class for token time settings

@Component // Indicates that this class is a Spring-managed component
public class JwtProvider {

    @Value("${jwt.secret}") // Injects the JWT secret key from application.properties
    private String secretKey; // Secret key used for signing JWT tokens

    private final long EXPIRATION_MS = 1000 * 60 * 60; // Token expiration time: 1 hour (in milliseconds)

    // Method to generate a JWT token with userId and role
    public String generateToken(String userId, String role) {
        return Jwts.builder() // Starts building the JWT token
                .setSubject(userId) // Sets the subject claim (typically the user identifier)
                .claim("role", role) // Adds custom claim for user role
                .setIssuedAt(new Date()) // Sets the token issuance time to current time
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS)) // Sets the token expiration time
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8))) // Signs the token with the secret key
                .compact(); // Compiles the token into a compact string
    }    
}
