package com.yourorg.leaderboard.adapter.in.dto; // Package declaration for leaderboard-related DTOs

import lombok.AllArgsConstructor; // Lombok annotation to generate a constructor with all fields
import lombok.Getter; // Lombok annotation to generate getter methods for all fields
import lombok.Setter; // Lombok annotation to generate setter methods for all fields

@Getter // Generates getter methods for userId and role
@Setter // Generates setter methods for userId and role
@AllArgsConstructor // Generates a constructor with userId and role as parameters
public class JwtUserInfoDto { // DTO class for storing user info extracted from JWT

    private String userId; // User identifier extracted from JWT
    private String role; // User role extracted from JWT (e.g., USER, ADMIN)
}
