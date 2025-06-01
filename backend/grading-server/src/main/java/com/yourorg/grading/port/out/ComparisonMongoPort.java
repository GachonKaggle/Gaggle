package com.yourorg.grading.port.out; // Defines the output port interface for MongoDB-related operations

// Interface for MongoDB persistence related to grading results and tasks
public interface ComparisonMongoPort {

    // Save grading result into MongoDB
    // Parameters:
    // - userId: The ID of the user who submitted the grading
    // - requestId: Unique identifier for this grading request
    // - loginId: The login ID of the user
    // - psnrAvg: The average PSNR value (image quality metric)
    // - ssimAvg: The average SSIM value (structural similarity metric)
    // - task: The grading task name or type
    void saveResultJPA(String userId, String requestId, String loginId, Double psnrAvg, Double ssimAvg, String task);

    // Save the task name to the task collection if it does not already exist
    // Parameters:
    // - token: JWT token from the user (not directly used in DB, possibly for validation elsewhere)
    // - taskName: Name of the task to be saved
    void saveTaskRepository(String token, String taskName);
}
