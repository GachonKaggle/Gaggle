package com.yourorg.grading.port.out; // Defines the output port interface for sending messages to Kafka

import com.yourorg.grading.adapter.out.dto.SendRequestDto; // DTO that contains file and metadata for Kafka transmission

// Interface for sending grading-related ZIP files to Kafka topics
public interface ComparisonProducerPort {

    // Sends a ZIP file for comparison processing via Kafka
    // Parameters:
    // - dto: Contains user info, request ID, task type, and the ZIP file in bytes
    void sendZipFile(SendRequestDto dto);

    // Sends a ZIP file for task-specific comparison (e.g., admin grading) via Kafka
    // Parameters:
    // - dto: Contains user info, request ID, task type, and the ZIP file in bytes
    void sendTaskZipFile(SendRequestDto dto);
}
