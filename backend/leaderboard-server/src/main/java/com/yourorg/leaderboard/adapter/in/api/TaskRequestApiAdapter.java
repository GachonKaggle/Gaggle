package com.yourorg.leaderboard.adapter.in.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Value; // Injects values from application.properties
import org.springframework.http.HttpStatus; // Provides HTTP status codes
import org.springframework.http.ResponseEntity; // Used to build HTTP responses
import org.springframework.web.bind.annotation.GetMapping; // Annotation for GET HTTP method
import org.springframework.web.bind.annotation.RequestHeader; // To extract headers from HTTP request
import org.springframework.web.bind.annotation.RequestMapping; // To map request URLs
import org.springframework.web.bind.annotation.RestController; // Marks this class as a REST controller

import com.yourorg.leaderboard.adapter.in.dto.JwtUserInfoDto; // DTO for user info extracted from JWT
import com.yourorg.leaderboard.adapter.in.dto.OurApiResponse; // Custom API response wrapper
import com.yourorg.leaderboard.adapter.in.dto.TaskListDto; // DTO for returning task list data
import com.yourorg.leaderboard.port.in.TaskRequestPort; // Port interface for retrieving tasks
import com.yourorg.leaderboard.util.JwtUtil; // Utility class for parsing JWT

import lombok.RequiredArgsConstructor; // Lombok: generates constructor for final fields

@RestController // Marks this class as a RESTful web controller
@RequestMapping("/api/leaderboard/task") // Base URL mapping for this controller
@RequiredArgsConstructor // Automatically generates constructor for final fields
public class TaskRequestApiAdapter {

    private final TaskRequestPort taskRequestPort; // Port to handle task-related requests

    @Value("${jwt.secret}") // Injects JWT secret from configuration
    private String secretKey;

    @GetMapping // Maps GET HTTP request to this method
    public ResponseEntity<OurApiResponse<List<TaskListDto>>> getTaskList(
        @RequestHeader("Authorization") String token // Extracts Authorization header (Bearer token)
    ) {
        // Extract user information (userId, role) from JWT token
        JwtUserInfoDto userInfo = JwtUtil.getUserInfoFromToken(token.replace("Bearer ", ""), secretKey);

        // Request task list from the use case (service layer)
        List<TaskListDto> dto = taskRequestPort.getTaskList();

        // If task list is null or empty, return failure response
        if (dto == null || dto.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST) // 400 Bad Request
                    .body(new OurApiResponse<>("fail", null, "랭킹 데이터가 없습니다.")); // Message in Korean (do not translate)
        }

        // If task list exists, return success response with data
        return ResponseEntity.ok(new OurApiResponse<>("success", dto, null));
    }
}
