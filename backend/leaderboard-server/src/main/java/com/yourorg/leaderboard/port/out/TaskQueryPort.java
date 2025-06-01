package com.yourorg.leaderboard.port.out; // Defines the package for outbound ports

import com.yourorg.leaderboard.adapter.in.dto.TaskListDto; // Import TaskListDto for returning task information
import java.util.List; // Import List interface from java.util package

// Outbound port interface for querying task data from the persistence layer
public interface TaskQueryPort {
    
    // Loads and returns a list of available tasks
    List<TaskListDto> loadTaskList();
}
