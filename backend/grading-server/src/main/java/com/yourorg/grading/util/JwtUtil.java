package com.yourorg.grading.util; // Package for utility classes

import io.jsonwebtoken.Claims; // Class for accessing JWT claims
import io.jsonwebtoken.Jwts; // Class for parsing JWTs
import io.jsonwebtoken.security.Keys; // Utility for generating signing keys
import java.nio.charset.StandardCharsets; // Used for character encoding
import com.yourorg.grading.adapter.in.dto.JwtUserInfoDto; // DTO for user information extracted from token

public class JwtUtil { // Utility class for JWT operations

    // Extracts userId and role from the JWT token
    public static JwtUserInfoDto getUserInfoFromToken(String token, String secretKey) {
        Claims claims = Jwts.parserBuilder() // Initialize JWT parser
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8))) // Set signing key
                .build() // Build parser
                .parseClaimsJws(token) // Parse the token
                .getBody(); // Get token body (claims)

        String userId = claims.getSubject(); // Get userId from 'sub' claim
        String role = claims.get("role", String.class); // Get role from custom 'role' claim

        return new JwtUserInfoDto(userId, role); // Return DTO with userId and role
    }
}
