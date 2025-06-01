package com.yourorg.leaderboard.port.in; // Package declaration for leaderboard inbound port

import java.util.List; // Import List interface from java.util
import com.yourorg.leaderboard.adapter.in.dto.TaskListDto; // Import the TaskList DTO

// Inbound port interface for retrieving available task list (application core input boundary)
public interface TaskRequestPort {

    // Returns a list of tasks available in the system for leaderboard or grading
    List<TaskListDto> getTaskList();
}
