package com.yourorg.leaderboard.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import com.yourorg.leaderboard.adapter.in.dto.TaskListDto;
import com.yourorg.leaderboard.adapter.out.repository.TaskMongoRepository;
import com.yourorg.leaderboard.port.out.TaskQueryPort;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TaskMongoAdapter implements TaskQueryPort {
    private final TaskMongoRepository taskMongoRepository;

    @Override
    public List<TaskListDto> loadTaskList() {
        return taskMongoRepository.findAllTaskNames()
                                  .stream()
                                  .map(taskNameOnly -> new TaskListDto(taskNameOnly.getTaskName()))
                                  .toList();
    }
}
