package com.yourorg.leaderboard.adapter.in.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yourorg.leaderboard.adapter.in.dto.JwtUserInfoDto;
import com.yourorg.leaderboard.adapter.in.dto.OurApiResponse;
import com.yourorg.leaderboard.adapter.in.dto.TaskListDto;
import com.yourorg.leaderboard.port.in.TaskRequestPort;
import com.yourorg.leaderboard.util.JwtUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/leaderboard/task")    
@RequiredArgsConstructor
public class TaskRequestApiAdapter {
    
    private final TaskRequestPort taskRequestPort;

    @Value("${jwt.secret}")
    private String secretKey;

    @GetMapping
    public ResponseEntity<OurApiResponse<List<TaskListDto>>> getTaskList(
        @RequestHeader("Authorization") String token
    ) {
        JwtUserInfoDto userInfo = JwtUtil.getUserInfoFromToken(token.replace("Bearer ", ""), secretKey);

        List<TaskListDto> dto = taskRequestPort.getTaskList();

        if (dto == null || dto.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new OurApiResponse<>("fail", null, "랭킹 데이터가 없습니다."));
        }
        return ResponseEntity.ok(new OurApiResponse<>("success", dto, null));
        
    }
}
