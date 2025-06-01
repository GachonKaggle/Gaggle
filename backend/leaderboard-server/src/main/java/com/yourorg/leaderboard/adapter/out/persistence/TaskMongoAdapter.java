package com.yourorg.leaderboard.adapter.out.persistence; // Package declaration for persistence layer

import lombok.RequiredArgsConstructor; // Lombok annotation to generate constructor for final fields
import org.springframework.stereotype.Component; // Marks this class as a Spring Bean component

import com.yourorg.leaderboard.adapter.in.dto.TaskListDto; // DTO for task list response
import com.yourorg.leaderboard.adapter.out.repository.TaskMongoRepository; // MongoDB repository interface for tasks
import com.yourorg.leaderboard.port.out.TaskQueryPort; // Output port interface for task querying
import java.util.List; // Import List interface

@Component // Registers this class as a Spring-managed component
@RequiredArgsConstructor // Generates constructor for final fields
public class TaskMongoAdapter implements TaskQueryPort { // Implements the output port interface

    private final TaskMongoRepository taskMongoRepository; // Repository for accessing task data in MongoDB

    @Override
    public List<TaskListDto> loadTaskList() {
        // Retrieves all task name documents from MongoDB, maps each to a TaskListDto, and returns the list
        return taskMongoRepository.findAllTaskNames() // Query to get all task name projections
                                  .stream() // Convert the list to a stream for processing
                                  .map(taskNameOnly -> new TaskListDto(taskNameOnly.getTaskName())) // Map to DTO
                                  .toList(); // Collect results into a list
    }
}
