package com.yourorg.leaderboard.domain.service;

import com.yourorg.leaderboard.port.out.LeaderBoardQueryPort; // Outbound Port
import com.yourorg.leaderboard.adapter.in.dto.LeaderBoardDto;
import com.yourorg.leaderboard.adapter.in.dto.TaskListDto;
import com.yourorg.leaderboard.port.in.LeaderBoardPort;
import com.yourorg.leaderboard.port.in.TaskRequestPort;
import com.yourorg.leaderboard.port.out.TaskQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaderBoardService implements LeaderBoardPort, TaskRequestPort {
    private final LeaderBoardQueryPort leaderBoardQueryPort; // Repository 역할은 Port가 담당
    private final TaskQueryPort taskQueryPort;

    @Override
    public List<LeaderBoardDto> getLeaderBoardsByTask(String task) {
        List<LeaderBoardDto> boards = leaderBoardQueryPort.loadLeaderBoardsByTask(task);
        return boards.stream()
            .map(lb -> new LeaderBoardDto(lb.getLoginId(), lb.getPsnrAvg(), lb.getSsimAvg(), task, lb.getRank(), lb.getDays()))
            .collect(Collectors.toList());
    }

    @Override
    public List<LeaderBoardDto> getLeaderBoardByUser(String userId) {
        List<TaskListDto> taskList = taskQueryPort.loadTaskList();
        List<LeaderBoardDto> result = new ArrayList<>();
        for (TaskListDto taskDto : taskList) {
            String task = taskDto.getTask();
            List<LeaderBoardDto> boards = leaderBoardQueryPort.loadLeaderBoardsByUserAndTask(userId, task);
            result.addAll(boards);
        }
        return result;
    }


    @Override
    public List<TaskListDto>  getTaskList() {
        List<TaskListDto> dto = taskQueryPort.loadTaskList();
        return dto.stream()
            .map(t -> new TaskListDto(t.getTask()))
            .collect(Collectors.toList());
    }
}
