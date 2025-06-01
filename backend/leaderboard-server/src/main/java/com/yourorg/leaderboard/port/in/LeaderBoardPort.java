package com.yourorg.leaderboard.port.in;

import com.yourorg.leaderboard.adapter.in.dto.LeaderBoardDto;
import java.util.List;

public interface LeaderBoardPort {
    List<LeaderBoardDto> getLeaderBoardsByTask(String task);
    List<LeaderBoardDto> getLeaderBoardByUser(String userId);
}
