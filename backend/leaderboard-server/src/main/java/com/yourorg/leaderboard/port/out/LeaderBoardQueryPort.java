package com.yourorg.leaderboard.port.out;

import com.yourorg.leaderboard.adapter.in.dto.LeaderBoardDto;
import java.util.List;

// 반드시 인터페이스!
public interface LeaderBoardQueryPort {
    List<LeaderBoardDto> loadLeaderBoardsByTask(String task);
    List<LeaderBoardDto> loadLeaderBoardsByUserAndTask(String userId, String task);
}
