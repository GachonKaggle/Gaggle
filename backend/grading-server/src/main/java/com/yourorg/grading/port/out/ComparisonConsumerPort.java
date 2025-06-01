package com.yourorg.grading.port.out; // Package for output ports in the hexagonal architecture

import java.util.Map; // Import for using Map type
import com.yourorg.grading.adapter.in.dto.OurApiResponse; // Import the common API response wrapper

// Interface for sending progress and result data to the client (usually through WebSocket or messaging)
public interface ComparisonConsumerPort {

    // Sends grading progress to the user via token
    // Parameters:
    // - token: The authentication token identifying the user
    // - progressDto: The progress data wrapped in OurApiResponse format
    void sendProgressToUser(String token, OurApiResponse<Map<String, Object>> progressDto);

    // Sends final grading result to the user via token
    // Parameters:
    // - token: The authentication token identifying the user
    // - resultDto: The result data wrapped in OurApiResponse format
    void sendResultToUser(String token, OurApiResponse<Map<String, Object>> resultDto);
}
