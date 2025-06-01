package com.yourorg.grading.adapter.out.dto;

import lombok.AllArgsConstructor; // Lombok annotation for generating all-args constructor
import lombok.Data;               // Lombok annotation for getter/setter, toString, equals, hashCode
import lombok.NoArgsConstructor;  // Lombok annotation for generating no-args constructor

@Data // Generates getters, setters, toString, equals, and hashCode methods
@NoArgsConstructor // No-argument constructor
@AllArgsConstructor // All-argument constructor
public class SendRequestDto {
    private String token;       // JWT token used for authentication
    private String userId;      // User's unique identifier
    private String loginId;     // User's login ID (optional in some constructors)
    private String requestId;   // Unique request ID for tracking the operation
    private String task;        // The name or type of the grading task
    private byte[] zipFile;     // Byte array of the uploaded ZIP file contents

    // Constructor without loginId (used in file upload logic)
    public SendRequestDto(String token, String userId, String requestId, String task, byte[] zipFile) {
        this.token = token;         // Set token
        this.userId = userId;       // Set user ID
        this.requestId = requestId; // Set request ID
        this.task = task;           // Set task name
        this.zipFile = zipFile;     // Set ZIP file content
    }
}
