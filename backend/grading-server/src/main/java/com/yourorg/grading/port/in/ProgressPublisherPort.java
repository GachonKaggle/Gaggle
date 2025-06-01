package com.yourorg.grading.port.in; // Defines the package for input ports (interfaces) in Hexagonal Architecture

import java.util.Map; // Import Map interface for handling key-value data structures
import com.yourorg.grading.adapter.in.dto.OurApiResponse; // Import custom API response wrapper

// Input port interface for publishing progress and result updates to users
public interface ProgressPublisherPort {

    // Sends progress information to the user via a communication channel (e.g., WebSocket)
    // Parameters:
    // - token: JWT token for identifying the user session
    // - progress: API response object containing progress information
    void sendProgressToUser(String token, OurApiResponse<Map<String, Object>> progress);

    // Sends the final result information to the user via a communication channel (e.g., WebSocket)
    // Parameters:
    // - token: JWT token for identifying the user session
    // - result: API response object containing result data
    void sendResultToUser(String token, OurApiResponse<Map<String, Object>> result);
}
