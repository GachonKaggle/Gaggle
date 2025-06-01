package com.yourorg.grading.adapter.in.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.yourorg.grading.port.in.ComparisonPort;
import com.yourorg.grading.adapter.out.dto.SendRequestDto;
import com.yourorg.grading.adapter.in.dto.JwtUserInfoDto;
import com.yourorg.grading.util.JwtUtil;
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
@RequestMapping("/api/grading")
@RequiredArgsConstructor
public class ComparisonRequestAdapter {

    private final ComparisonPort comparisionRequestPort;

    @Value("${jwt.secret}")
    private String secretKey;

    @Operation(
        summary = "채점 파일 업로드 및 채점 요청",
        description = "채점을 원하는 파일과 채점 유형(task)을 업로드합니다. 업로드 성공 시 OurApiResponse에 requestId가 문자열로 반환됩니다.",
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
                      "data": "550e8400-e29b-41d4-a716-446655440000",
                      "message": "업로드 성공"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류 (ex: 파일 파싱, 서비스 오류 등)",
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

            String requestId = UUID.randomUUID().toString();

            SendRequestDto dto = new SendRequestDto(token, userId, requestId, task, file.getBytes());

            comparisionRequestPort.comparisionRequest(dto);

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
