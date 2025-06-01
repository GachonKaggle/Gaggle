package com.yourorg.leaderboard.port.out; // Package declaration for outbound port

import com.yourorg.leaderboard.adapter.in.dto.LeaderBoardDto; // Import LeaderBoardDto for returning leaderboard data
import java.util.List; // Import List from java.util

// This is an outbound port interface used to abstract leaderboard data retrieval logic
public interface LeaderBoardQueryPort {

    // Loads the leaderboard data for all users for a specific task, sorted by psnrAvg
    List<LeaderBoardDto> loadLeaderBoardsByTask(String task);

    // Loads the leaderboard data for a specific user in a given task context
    List<LeaderBoardDto> loadLeaderBoardsByUserAndTask(String userId, String task);
}
