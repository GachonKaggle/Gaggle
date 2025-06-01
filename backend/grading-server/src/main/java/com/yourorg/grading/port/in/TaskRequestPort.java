package com.yourorg.grading.port.in; // Package for input ports in the hexagonal architecture

import com.yourorg.grading.adapter.out.dto.SendRequestDto; // Import the DTO used to carry task request data

// Interface for handling task request logic from the web layer to the application/service layer
public interface TaskRequestPort {

    // Method to process a task grading request
    // Parameters:
    // - dto: Data Transfer Object containing user ID, token, task name, zip file, and request ID
    void taskRequest(SendRequestDto dto);
}
