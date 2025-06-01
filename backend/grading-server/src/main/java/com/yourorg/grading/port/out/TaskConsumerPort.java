package com.yourorg.grading.port.out; // Defines the output port interface for consuming task-related messages

import java.util.Map; // Used for generic key-value mappings

import com.yourorg.grading.adapter.in.dto.OurApiResponse; // Wrapper class for standardized API responses

// Interface for sending task status updates to the client (e.g., via WebSocket)
public interface TaskConsumerPort {

    // Sends the task processing status to the user
    // Parameters:
    // - token: JWT token used to identify the user
    // - taskResult: API response containing task result data
    void sendTaskStatus(String token, OurApiResponse<Map<String, Object>> taskResult);
}
