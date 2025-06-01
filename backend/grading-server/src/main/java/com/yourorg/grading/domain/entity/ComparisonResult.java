package com.yourorg.grading.domain.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;

// Lombok annotations for generating boilerplate code (getters, setters, constructors)
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor

// Marks this class as a MongoDB document in the "comparision_results" collection
@Document(collection = "comparision_results")
public class ComparisonResult {

    @Id  // Primary key for MongoDB
    private String requestId;

    // ID of the user who submitted the request
    private String userId;

    // Authentication token (optional for saving trace/log)
    private String token;

    // User's login ID (e.g., email or username)
    private String loginId;

    // Average PSNR (Peak Signal-to-Noise Ratio)
    private Double psnrAvg;

    // Average SSIM (Structural Similarity Index)
    private Double ssimAvg;

    // Task name or category (e.g., "image-compare", "video-compare")
    private String task;

    // Timestamp or date string when the result was saved
    private String days;

    // Custom constructor excluding token field (used during result saving)
    public ComparisonResult(String userId, String requestId, String loginId, Double psnrAvg, Double ssimAvg, String task, String days) {
        this.userId = userId;
        this.requestId = requestId;
        this.loginId = loginId;
        this.psnrAvg = psnrAvg;
        this.ssimAvg = ssimAvg;
        this.task = task;
        this.days = days;
    }
}
