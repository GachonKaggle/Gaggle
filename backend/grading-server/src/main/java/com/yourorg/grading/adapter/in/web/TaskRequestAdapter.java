package com.yourorg.grading.adapter.in.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.yourorg.grading.adapter.out.dto.SendRequestDto;
import com.yourorg.grading.adapter.in.dto.JwtUserInfoDto;
import com.yourorg.grading.util.JwtUtil;
import com.yourorg.grading.port.in.TaskRequestPort;
import org.springframework.beans.factory.annotation.Value;

import java.util.UUID;

import com.yourorg.grading.adapter.in.dto.OurApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/grading/task")
@RequiredArgsConstructor
public class TaskRequestAdapter {

    private final TaskRequestPort taskRequestPort;

    @Value("${jwt.secret}")
    private String secretKey;

    @Operation(
        summary = "관리자 채점 파일 업로드 및 채점 요청",
        description = "관리자(ROLE: ADMIN)만 채점 파일 업로드가 가능합니다. 업로드 성공 시 OurApiResponse에 requestId(문자열)이 data로 반환됩니다.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "업로드 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = OurApiResponse.class),
                examples = @ExampleObject(
                    name = "업로드 성공 예시",
                    value = """
                    {
                      "status": "success",
                      "data": "c1a9b9fa-00bb-4e82-969a-0adf06a5de29",
                      "message": "업로드 성공"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "권한 없음(관리자 아님)",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = OurApiResponse.class),
                examples = @ExampleObject(
                    name = "권한 없음 예시",
                    value = """
                    {
                      "status": "fail",
                      "data": null,
                      "message": "접근 권한이 없습니다."
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "서버 내부 에러",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = OurApiResponse.class),
                examples = @ExampleObject(
                    name = "실패 예시",
                    value = """
                    {
                      "status": "fail",
                      "data": null,
                      "message": "처리 실패: 내부 오류 메시지"
                    }
                    """
                )
            )
        )
    })
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<OurApiResponse<String>> comparison(
            @RequestHeader("Authorization") String token,
            @RequestParam("file") MultipartFile file,
            @RequestParam("task") String task
    ) {
        try {
            JwtUserInfoDto userInfo = JwtUtil.getUserInfoFromToken(token.replace("Bearer ", ""), secretKey);
            String userId = userInfo.getUserId();
            String role = userInfo.getRole();

            // 관리자만 허용: ROLE이 USER면 거부 (ADMIN만 통과)
            if ("USER".equals(role)) {
                return ResponseEntity
                        .status(403)
                        .body(new OurApiResponse<>(
                            "fail",
                            null,
                            "접근 권한이 없습니다."
                        ));
            }

            String requestId = UUID.randomUUID().toString();

            SendRequestDto dto = new SendRequestDto(
                token,
                userId,
                requestId,
                task,
                file.getBytes()
            );

            taskRequestPort.taskRequest(dto);

            return ResponseEntity.ok(
                new OurApiResponse<>(
                    "success",
                    requestId,
                    "업로드 성공"
                )
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                new OurApiResponse<>(
                    "fail",
                    null,
                    "처리 실패: " + e.getMessage()
                )
            );
        }
    }
}
