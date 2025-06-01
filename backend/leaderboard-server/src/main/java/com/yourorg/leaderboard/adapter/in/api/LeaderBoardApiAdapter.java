package com.yourorg.leaderboard.adapter.in.api;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import com.yourorg.leaderboard.port.in.LeaderBoardPort;
import com.yourorg.leaderboard.adapter.in.dto.LeaderBoardDto;
import com.yourorg.leaderboard.adapter.in.dto.JwtUserInfoDto;
import com.yourorg.leaderboard.util.JwtUtil;
import com.yourorg.leaderboard.adapter.in.dto.OurApiResponse;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController // Declares this class as a REST controller
@RequestMapping("/api/leaderboard") // Base URL for leaderboard APIs
@RequiredArgsConstructor // Lombok: generates constructor for final fields
public class LeaderBoardApiAdapter {

    private final LeaderBoardPort leaderBoardPort; // Port interface for leaderboard service logic

    @Value("${jwt.secret}") // Injects JWT secret key from application configuration
    private String secretKey;

    // [1] API to get all users' leaderboard by task
    @Operation(
        summary = "전체 유저 랭킹 조회", // Summary in Korean
        description = "특정 task의 전체 유저 랭킹을 조회합니다.", // Description in Korean
        security = @SecurityRequirement(name = "bearerAuth") // JWT-based security
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", // HTTP 200 OK response
            description = "랭킹 데이터 조회 성공", // Success message
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = OurApiResponse.class),
                examples = @ExampleObject(
                    name = "성공 예시",
                    value = """
                    {
                      "status": "success",
                      "data": [
                        {
                          "loginId": "userA",
                          "psnrAvg": 38.9,
                          "task": "mock",
                          "rank": 1
                        },
                        {
                          "loginId": "userB",
                          "psnrAvg": 37.5,
                          "task": "mock",
                          "rank": 2
                        }
                      ],
                      "message": null
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400", // HTTP 400 Bad Request if data is missing
            description = "랭킹 데이터 없음", // No data found
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = OurApiResponse.class),
                examples = @ExampleObject(
                    name = "데이터 없음 예시",
                    value = """
                    {
                      "status": "fail",
                      "data": null,
                      "message": "랭킹 데이터가 없습니다."
                    }
                    """
                )
            )
        )
    })
    @GetMapping // HTTP GET endpoint
    public ResponseEntity<OurApiResponse<List<LeaderBoardDto>>> getLeaderBoard(
            @RequestHeader("Authorization") String token, // JWT token from Authorization header
            @RequestParam("task") String task) { // Task name to filter rankings

        // Parse user info from token
        JwtUserInfoDto userInfo = JwtUtil.getUserInfoFromToken(token.replace("Bearer ", ""), secretKey);

        // Retrieve leaderboard list for the task
        List<LeaderBoardDto> dto = leaderBoardPort.getLeaderBoardsByTask(task);

        // If no data is found, return 400 with fail message
        if (dto == null || dto.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new OurApiResponse<>("fail", null, "랭킹 데이터가 없습니다."));
        }

        // If data exists, return success response
        return ResponseEntity.ok(new OurApiResponse<>("success", dto, null));
    }

    // [2] API to get leaderboard info for the current user based on token
    @Operation(
        summary = "개별 유저 랭킹 조회", // Summary in Korean
        description = "JWT 토큰 내 userId 기준, 해당 유저의 특정 task 랭킹을 조회합니다.", // Description in Korean
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", // Success response
            description = "해당 유저의 랭킹 데이터 조회 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = OurApiResponse.class),
                examples = @ExampleObject(
                    name = "성공 예시",
                    value = """
                    {
                      "status": "success",
                      "data": [
                        {
                          "loginId": "userB",
                          "psnrAvg": 37.5,
                          "task": "mock",
                          "rank": 2
                        }
                      ],
                      "message": null
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400", // No data found for user
            description = "해당 유저의 랭킹 데이터 없음",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = OurApiResponse.class),
                examples = @ExampleObject(
                    name = "데이터 없음 예시",
                    value = """
                    {
                      "status": "fail",
                      "data": null,
                      "message": "해당 유저의 랭킹 데이터가 없습니다."
                    }
                    """
                )
            )
        )
    })
    @GetMapping("/user") // GET endpoint to retrieve user's own rank
    public ResponseEntity<OurApiResponse<List<LeaderBoardDto>>> getLeaderBoardByUser(
            @RequestHeader("Authorization") String token) {

        // Extract user info from JWT token
        JwtUserInfoDto userInfo = JwtUtil.getUserInfoFromToken(token.replace("Bearer ", ""), secretKey);

        // Get user's leaderboard record
        List<LeaderBoardDto> dto = leaderBoardPort.getLeaderBoardByUser(userInfo.getUserId());

        // If not found, return fail response
        if (dto == null || dto.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new OurApiResponse<>("fail", null, "해당 유저의 랭킹 데이터가 없습니다."));
        }

        // Return success response
        return ResponseEntity.ok(new OurApiResponse<>("success", dto, null));
    }
}
