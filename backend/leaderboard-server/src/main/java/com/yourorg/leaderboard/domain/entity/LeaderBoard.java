package com.yourorg.leaderboard.domain.entity; // Package declaration for the domain entity layer

import lombok.AllArgsConstructor; // Lombok annotation to generate a constructor with all fields
import lombok.Getter; // Lombok annotation to generate getters for all fields
import lombok.Setter; // Lombok annotation to generate setters for all fields
import org.springframework.data.annotation.Id; // Marks the field as the primary ID in MongoDB
import org.springframework.data.mongodb.core.mapping.Document; // Indicates this class is a MongoDB document
import lombok.NoArgsConstructor; // Lombok annotation to generate a no-argument constructor

@Getter @Setter // Generate getters and setters for all fields
@NoArgsConstructor // Generate a default constructor (no arguments)
@AllArgsConstructor // Generate a constructor with all arguments
@Document(collection = "comparision_results") // Map this class to the "comparision_results" collection in MongoDB
public class LeaderBoard {

    @Id // Marks this field as the document ID
    private String requestId; // Unique ID for the grading request
    private String userId; // ID of the user who submitted the task
    private String token; // JWT token of the user (optional or unused)
    private String loginId; // User's login ID (username or email)
    private Double psnrAvg; // Average PSNR (Peak Signal-to-Noise Ratio) value
    private Double ssimAvg; // Average SSIM (Structural Similarity Index) value
    private String task; // Task name or identifier
    private String days; // Date string (usually when the grading was done)
    private Long rank; // User's rank in the leaderboard for the task

    // Custom constructor used when token is not needed
    public LeaderBoard(String userId, String requestId, String loginId, Double psnrAvg, Double ssimAvg, String task, String days, Long rank) {
        this.userId = userId; // Assign userId
        this.requestId = requestId; // Assign requestId
        this.loginId = loginId; // Assign loginId
        this.psnrAvg = psnrAvg; // Assign PSNR average
        this.ssimAvg = ssimAvg; // Assign SSIM average
        this.task = task; // Assign task name
        this.days = days; // Assign date
        this.rank = rank; // Assign rank
    }
}
