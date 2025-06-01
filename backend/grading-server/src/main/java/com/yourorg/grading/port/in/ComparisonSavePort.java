package com.yourorg.grading.port.in; // Defines the package for input (incoming) port interfaces

// Port interface for saving comparison results (used on the input side of Hexagonal Architecture)
public interface ComparisonSavePort {

    // Method to save the result of a comparison
    // Parameters:
    // - userId: ID of the user who submitted the request
    // - requestId: Unique ID for this comparison request
    // - loginId: Login ID of the user
    // - psnrAvg: Average PSNR (Peak Signal-to-Noise Ratio) result
    // - ssimAvg: Average SSIM (Structural Similarity Index Measure) result
    // - task: Task identifier or name associated with the request
    void saveResult(String userId, String requestId, String loginId, Double psnrAvg, Double ssimAvg, String task);
}
