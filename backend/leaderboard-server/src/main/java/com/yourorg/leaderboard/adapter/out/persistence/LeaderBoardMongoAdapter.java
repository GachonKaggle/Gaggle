package com.yourorg.leaderboard.adapter.out.persistence; // Package declaration for persistence adapter

import com.yourorg.leaderboard.port.out.LeaderBoardQueryPort; // Port interface for leaderboard query
import com.yourorg.leaderboard.domain.entity.LeaderBoard; // Domain entity
import com.yourorg.leaderboard.adapter.in.dto.LeaderBoardDto; // DTO for API response
import com.yourorg.leaderboard.adapter.out.repository.LeaderBoardMongoRepository; // MongoDB repository
import com.yourorg.leaderboard.util.DateUtils; // Utility class for date formatting

import lombok.RequiredArgsConstructor; // Lombok annotation for constructor injection
import org.springframework.stereotype.Component; // Marks this class as a Spring component

import java.util.Collections; // Utility class for empty collections (not used here)
import java.util.List; // List interface
import java.util.Optional; // Optional class (not used here)
import java.util.stream.Collectors; // Collector for streams
import java.util.stream.IntStream; // IntStream for index-based looping

@Component // Registers this class as a Spring component
@RequiredArgsConstructor // Generates constructor for final fields
public class LeaderBoardMongoAdapter implements LeaderBoardQueryPort { // Implements port interface for leaderboard queries

    private final LeaderBoardMongoRepository leaderBoardMongoRepository; // Injects MongoDB repository for leaderboard data

    // Load full leaderboard for a given task, ordered by PSNR descending
    @Override
    public List<LeaderBoardDto> loadLeaderBoardsByTask(String task) {
        // Query all leaderboard entries for the given task, ordered by PSNR descending
        List<LeaderBoard> entities = leaderBoardMongoRepository.findByTaskOrderByPsnrAvgDesc(task);

        // Map each entity to a DTO while assigning dynamic ranks based on position
        return IntStream.range(0, entities.size()) // Loop over indices
                .mapToObj(i -> { // For each index
                    LeaderBoard e = entities.get(i); // Get entity at index
                    long rank = i + 1L; // Calculate rank (1-based)
                    String days = DateUtils.getDaysAgoString(e.getDays()); // Format date into "days ago" string
                    return new LeaderBoardDto(e.getLoginId(), e.getPsnrAvg(), e.getSsimAvg(), e.getTask(), rank, days); // Create DTO
                })
                .collect(Collectors.toList()); // Collect all DTOs into a list
    }

    // Load current ranking of a specific user for a given task
    @Override
    public List<LeaderBoardDto> loadLeaderBoardsByUserAndTask(String userId, String task) {
        // Load all leaderboard entries for the task
        List<LeaderBoard> entities = leaderBoardMongoRepository.findByTaskOrderByPsnrAvgDesc(task);

        // Filter the entries to find matching userId and map to DTOs
        return IntStream.range(0, entities.size()) // Loop over indices
                .filter(i -> { // Filter entries by userId
                    String dbUserId = entities.get(i).getUserId(); // Get userId from entity
                    return dbUserId != null && dbUserId.equals(userId); // Match with input userId
                })
                .mapToObj(i -> { // Convert to DTO
                    LeaderBoard e = entities.get(i); // Get entity
                    long rank = i + 1L; // Calculate rank
                    String days = DateUtils.getDaysAgoString(e.getDays()); // Format date
                    return new LeaderBoardDto( // Build DTO
                        e.getLoginId(),
                        e.getPsnrAvg(),
                        e.getSsimAvg(),
                        e.getTask(),
                        rank,
                        days
                    );
                })
                .limit(5) // ★ Limit to a maximum of 5 results
                .collect(Collectors.toList()); // Collect to list
    }
}
