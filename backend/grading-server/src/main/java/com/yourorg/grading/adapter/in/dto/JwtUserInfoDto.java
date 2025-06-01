package com.yourorg.grading.adapter.in.dto;  // Package declaration

import lombok.AllArgsConstructor;  // Lombok annotation to generate an all-args constructor
import lombok.Getter;              // Lombok annotation to generate getter methods
import lombok.Setter;              // Lombok annotation to generate setter methods

@Getter  // Lombok will generate getters for all fields
@Setter  // Lombok will generate setters for all fields
@AllArgsConstructor  // Lombok will generate a constructor with all fields as parameters
public class JwtUserInfoDto {
    private String userId;  // ID of the user extracted from JWT
    private String role;    // Role of the user (e.g., "ADMIN", "USER")
}
