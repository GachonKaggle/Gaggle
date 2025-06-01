package com.yourorg.leaderboard.domain.service; // Package declaration for service layer

import com.yourorg.leaderboard.port.out.LeaderBoardQueryPort; // Outbound port for querying leaderboard data
import com.yourorg.leaderboard.adapter.in.dto.LeaderBoardDto; // DTO for leaderboard data
import com.yourorg.leaderboard.adapter.in.dto.TaskListDto; // DTO for task list data
import com.yourorg.leaderboard.port.in.LeaderBoardPort; // Inbound port for leaderboard service
import com.yourorg.leaderboard.port.in.TaskRequestPort; // Inbound port for task service
import com.yourorg.leaderboard.port.out.TaskQueryPort; // Outbound port for querying task data
import lombok.RequiredArgsConstructor; // Lombok annotation to generate constructor for final fields
import org.springframework.stereotype.Service; // Marks this class as a Spring service component

import java.util.ArrayList; // List implementation for aggregation
import java.util.List; // Java interface for lists
import java.util.stream.Collectors; // Utility for stream operations

@Service // Marks this class as a service bean for dependency injection
@RequiredArgsConstructor // Automatically injects required final dependencies via constructor
public class LeaderBoardService implements LeaderBoardPort, TaskRequestPort {

    private final LeaderBoardQueryPort leaderBoardQueryPort; // Port to query leaderboard information from the database
    private final TaskQueryPort taskQueryPort; // Port to query task list information

    // Retrieves all leaderboard entries for a given task
    @Override
    public List<LeaderBoardDto> getLeaderBoardsByTask(String task) {
        List<LeaderBoardDto> boards = leaderBoardQueryPort.loadLeaderBoardsByTask(task); // Fetch leaderboard from DB
        return boards.stream() // Stream the list
            .map(lb -> new LeaderBoardDto( // Map each entity to DTO
                lb.getLoginId(),
                lb.getPsnrAvg(),
                lb.getSsimAvg(),
                task,
                lb.getRank(),
                lb.getDays()
            ))
            .collect(Collectors.toList()); // Collect back into list
    }

    // Retrieves leaderboard ranking entries for a specific user across all tasks
    @Override
    public List<LeaderBoardDto> getLeaderBoardByUser(String userId) {
        List<TaskListDto> taskList = taskQueryPort.loadTaskList(); // Get all available tasks
        List<LeaderBoardDto> result = new ArrayList<>(); // Prepare result list

        // Loop through each task and fetch leaderboard for that user
        for (TaskListDto taskDto : taskList) {
            String task = taskDto.getTask(); // Extract task name
            List<LeaderBoardDto> boards = leaderBoardQueryPort.loadLeaderBoardsByUserAndTask(userId, task); // Query by user + task
            result.addAll(boards); // Add all matching leaderboard records to result
        }
        return result; // Return final aggregated list
    }

    // Retrieves the list of available tasks
    @Override
    public List<TaskListDto> getTaskList() {
        List<TaskListDto> dto = taskQueryPort.loadTaskList(); // Query the task list from DB
        return dto.stream() // Stream the list
            .map(t -> new TaskListDto(t.getTask())) // Create new DTOs to ensure immutability
            .collect(Collectors.toList()); // Collect as list
    }
}
