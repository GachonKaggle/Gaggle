package com.yourorg.leaderboard.adapter.in.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class TaskListDto {
    private String task;

    public static TaskListDto fromEntity(String task) {
        TaskListDto dto = new TaskListDto();
        dto.setTask(task);
        return dto;
    }
}
