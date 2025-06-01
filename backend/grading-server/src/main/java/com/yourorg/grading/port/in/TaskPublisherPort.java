package com.yourorg.grading.port.in; // Package for input ports in the hexagonal architecture

import java.util.Map; // Import Java Map for key-value data handling

import com.yourorg.grading.adapter.in.dto.OurApiResponse; // Import custom response wrapper used throughout the API

// Interface defining operations related to publishing or saving task status
public interface TaskPublisherPort {

    // Sends task execution status to the user (typically via WebSocket or messaging system)
    // Parameters:
    // - token: User authentication token (usually JWT)
    // - taskResult: Wrapped response object containing the task status data
    void sendTaskStatus(String token, OurApiResponse<Map<String, Object>> taskResult);

    // Saves task metadata (e.g., task name) to the persistence layer
    // Parameters:
    // - token: User authentication token (may be used to identify ownership)
    // - taskName: The name of the task to be saved
    void saveTask(String token, String taskName);
}
