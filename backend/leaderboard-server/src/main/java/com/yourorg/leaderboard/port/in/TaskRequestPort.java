package com.yourorg.leaderboard.port.in;

import java.util.List;
import com.yourorg.leaderboard.adapter.in.dto.TaskListDto;

public interface TaskRequestPort {
    List<TaskListDto> getTaskList();
}
