package com.yourorg.leaderboard.port.out;

import com.yourorg.leaderboard.adapter.in.dto.TaskListDto;
import java.util.List;

public interface TaskQueryPort {
    List<TaskListDto> loadTaskList();
}
