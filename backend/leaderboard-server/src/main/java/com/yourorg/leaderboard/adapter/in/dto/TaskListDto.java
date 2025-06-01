package com.yourorg.leaderboard.adapter.in.dto;

import lombok.AllArgsConstructor; // Lombok annotation to generate a constructor with all arguments
import lombok.Getter; // Lombok annotation to generate getter methods
import lombok.RequiredArgsConstructor; // Lombok annotation to generate a constructor with required (final) fields
import lombok.Setter; // Lombok annotation to generate setter methods

@Getter // Generates getter methods for all fields
@Setter // Generates setter methods for all fields
@RequiredArgsConstructor // Generates a constructor for final fields only (none in this case, so acts as no-arg)
@AllArgsConstructor // Generates a constructor with all declared fields
public class TaskListDto { // DTO class representing a single task entry in a list

    private String task; // Name of the task

    // Static factory method to create TaskListDto from a task name (string)
    public static TaskListDto fromEntity(String task) {
        TaskListDto dto = new TaskListDto(); // Create an empty DTO instance
        dto.setTask(task); // Set the task name
        return dto; // Return the populated DTO
    }
}
