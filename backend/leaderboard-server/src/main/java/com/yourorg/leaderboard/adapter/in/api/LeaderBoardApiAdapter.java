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

@RestController
@RequestMapping("/api/leaderboard")
@RequiredArgsConstructor
public class LeaderBoardApiAdapter {

    private final LeaderBoardPort leaderBoardPort;

    @Value("${jwt.secret}")
    private String secretKey;

    @Operation(
        summary = "전체 유저 랭킹 조회",
        description = "특정 task의 전체 유저 랭킹을 조회합니다.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "랭킹 데이터 조회 성공",
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
            responseCode = "400",
            description = "랭킹 데이터 없음",
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
    @GetMapping
    public ResponseEntity<OurApiResponse<List<LeaderBoardDto>>> getLeaderBoard(
            @RequestHeader("Authorization") String token,
            @RequestParam("task") String task) {

        JwtUserInfoDto userInfo = JwtUtil.getUserInfoFromToken(token.replace("Bearer ", ""), secretKey);

        List<LeaderBoardDto> dto = leaderBoardPort.getLeaderBoardsByTask(task);

        if (dto == null || dto.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new OurApiResponse<>("fail", null, "랭킹 데이터가 없습니다."));
        }
        return ResponseEntity.ok(new OurApiResponse<>("success", dto, null));
    }

    @Operation(
        summary = "개별 유저 랭킹 조회",
        description = "JWT 토큰 내 userId 기준, 해당 유저의 특정 task 랭킹을 조회합니다.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
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
            responseCode = "400",
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
    @GetMapping("/user")
    public ResponseEntity<OurApiResponse<List<LeaderBoardDto>>> getLeaderBoardByUser(
            @RequestHeader("Authorization") String token) {

        JwtUserInfoDto userInfo = JwtUtil.getUserInfoFromToken(token.replace("Bearer ", ""), secretKey);
        List<LeaderBoardDto> dto = leaderBoardPort.getLeaderBoardByUser(userInfo.getUserId());

        if (dto == null || dto.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new OurApiResponse<>("fail", null, "해당 유저의 랭킹 데이터가 없습니다."));
        }
        return ResponseEntity.ok(new OurApiResponse<>("success", dto, null));
    }
}
