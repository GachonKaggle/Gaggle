package com.yourorg.grading.port.in; // Defines the package for incoming (input) port interfaces

import com.yourorg.grading.adapter.out.dto.SendRequestDto; // Imports the DTO used for sending request data

// Port interface for handling comparison requests (input side of Hexagonal Architecture)
public interface ComparisonPort {
    
    // Method to request a comparison process with the given data transfer object
    void comparisionRequest(SendRequestDto dto);
}
