package com.yourorg.leaderboard.adapter.in.dto; // Package declaration for leaderboard-related DTOs

import lombok.AllArgsConstructor; // Lombok annotation to generate a constructor with all fields
import lombok.Getter; // Lombok annotation to generate getter methods
import lombok.RequiredArgsConstructor; // Lombok annotation to generate a constructor with required (final) fields only
import lombok.Setter; // Lombok annotation to generate setter methods
import com.yourorg.leaderboard.domain.entity.LeaderBoard; // Import domain entity LeaderBoard

@Getter // Generates getter methods for all fields
@Setter // Generates setter methods for all fields
@RequiredArgsConstructor // Generates a no-argument constructor for final fields (not used in this case but required by some frameworks)
@AllArgsConstructor // Generates a constructor with all fields
public class LeaderBoardDto { // DTO for exposing leaderboard data to API clients

    private String loginId; // User login ID
    private Double psnrAvg; // Average PSNR score
    private Double ssimAvg; // Average SSIM score
    private String task; // Task name
    private Long rank; // Ranking position
    private String days; // Date string when the score was recorded

    public static LeaderBoardDto fromEntity(LeaderBoard leaderboard) { // Converts LeaderBoard entity to DTO
        LeaderBoardDto dto = new LeaderBoardDto(); // Create a new DTO instance
        dto.setLoginId(leaderboard.getLoginId()); // Set loginId from entity
        dto.setPsnrAvg(leaderboard.getPsnrAvg()); // Set psnrAvg from entity
        dto.setSsimAvg(leaderboard.getSsimAvg()); // Set ssimAvg from entity
        dto.setTask(leaderboard.getTask()); // Set task from entity
        dto.setRank(leaderboard.getRank()); // Set rank from entity
        dto.setDays(leaderboard.getDays()); // Set days from entity
        return dto; // Return the populated DTO
    }
}
