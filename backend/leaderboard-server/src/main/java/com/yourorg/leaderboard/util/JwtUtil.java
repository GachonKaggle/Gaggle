package com.yourorg.leaderboard.util; // Utility package for JWT-related functions

import io.jsonwebtoken.Claims; // Import JWT claims class
import io.jsonwebtoken.Jwts; // Import JWT parser
import io.jsonwebtoken.security.Keys; // Import for secure key handling
import java.nio.charset.StandardCharsets; // Import for UTF-8 encoding
import com.yourorg.leaderboard.adapter.in.dto.JwtUserInfoDto; // Import custom DTO for user info

public class JwtUtil { // Utility class for handling JWT token parsing

    // Extract userId and role from a JWT token
    public static JwtUserInfoDto getUserInfoFromToken(String token, String secretKey) {
        // Parse the JWT token to extract the claims (payload)
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8))) // Set signing key using secret
                .build() // Build the parser
                .parseClaimsJws(token) // Parse the signed JWT token
                .getBody(); // Get the body containing claims

        String userId = claims.getSubject(); // Retrieve userId from the "sub" (subject) field
        String role = claims.get("role", String.class); // Retrieve role from the "role" claim field

        // Return the extracted userId and role as a DTO
        return new JwtUserInfoDto(userId, role);
    }
}
