package com.yourorg.leaderboard.port.in; // Defines the inbound port for leaderboard use cases

import com.yourorg.leaderboard.adapter.in.dto.LeaderBoardDto; // Import the LeaderBoard Data Transfer Object
import java.util.List; // Import List interface

// Inbound port interface for retrieving leaderboard data (application core input boundary)
public interface LeaderBoardPort {

    // Retrieves the leaderboard for a specific task (e.g., all users ranked by performance)
    List<LeaderBoardDto> getLeaderBoardsByTask(String task);

    // Retrieves the leaderboard entries for a specific user across multiple tasks
    List<LeaderBoardDto> getLeaderBoardByUser(String userId);
}
