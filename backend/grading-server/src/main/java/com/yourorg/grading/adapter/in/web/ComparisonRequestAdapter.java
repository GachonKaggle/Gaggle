package com.yourorg.grading.adapter.in.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*; // For REST API annotations
import org.springframework.web.multipart.MultipartFile; // For handling file uploads
import com.yourorg.grading.port.in.ComparisonPort; // Interface for comparison service
import com.yourorg.grading.adapter.out.dto.SendRequestDto; // DTO for sending request
import com.yourorg.grading.adapter.in.dto.JwtUserInfoDto; // DTO to hold user info from JWT
import com.yourorg.grading.util.JwtUtil; // Utility class for JWT operations
import org.springframework.beans.factory.annotation.Value; // For injecting property values

import java.util.UUID; // For generating unique request ID

import com.yourorg.grading.adapter.in.dto.OurApiResponse; // Standard API response wrapper

import io.swagger.v3.oas.annotations.Operation; // Swagger annotation for describing API
import io.swagger.v3.oas.annotations.responses.ApiResponse; // Swagger response annotation
import io.swagger.v3.oas.annotations.responses.ApiResponses; // Container for multiple responses
import io.swagger.v3.oas.annotations.media.Content; // Swagger content type
import io.swagger.v3.oas.annotations.media.Schema; // Swagger schema description
import io.swagger.v3.oas.annotations.media.ExampleObject; // Swagger example object
import io.swagger.v3.oas.annotations.security.SecurityRequirement; // Swagger security annotation

@RestController // Marks this class as a REST controller
@RequestMapping("/api/grading") // Maps requests to /api/grading
@RequiredArgsConstructor // Generates constructor for final fields
public class ComparisonRequestAdapter {

    private final ComparisonPort comparisionRequestPort; // Port to delegate comparison logic

    @Value("${jwt.secret}") // Injects JWT secret key from properties
    private String secretKey;

    @Operation(
        summary = "채점 파일 업로드 및 채점 요청", // API summary shown in Swagger
        description = "채점을 원하는 파일과 채점 유형(task)을 업로드합니다. 업로드 성공 시 OurApiResponse에 requestId가 문자열로 반환됩니다.", // API details
        security = @SecurityRequirement(name = "bearerAuth") // Indicates JWT security is required
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", // HTTP 200 success response
            description = "업로드 성공", // Description for successful upload
            content = @Content(
                mediaType = "application/json", // JSON response
                schema = @Schema(implementation = OurApiResponse.class), // Response schema
                examples = @ExampleObject(
                    name = "업로드 성공 예시", // Example name
                    value = """
                    {
                      "status": "success",
                      "data": "550e8400-e29b-41d4-a716-446655440000",
                      "message": "업로드 성공"
                    }
                    """ // Sample success response
                )
            )
        ),
        @ApiResponse(
            responseCode = "500", // HTTP 500 error response
            description = "서버 내부 오류 (ex: 파일 파싱, 서비스 오류 등)", // Description for internal server error
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = OurApiResponse.class),
                examples = @ExampleObject(
                    name = "실패 예시", // Example name
                    value = """
                    {
                      "status": "fail",
                      "data": null,
                      "message": "처리 실패: 내부 오류 메시지"
                    }
                    """ // Sample failure response
                )
            )
        )
    })
    @PostMapping(consumes = {"multipart/form-data"}) // Accepts multipart/form-data (file upload)
    public ResponseEntity<OurApiResponse<String>> comparison(
            @RequestHeader("Authorization") String token, // Authorization header (JWT)
            @RequestParam("file") MultipartFile file, // Uploaded file
            @RequestParam("task") String task // Task name
    ) {
        try {
            // Extract user info from JWT token
            JwtUserInfoDto userInfo = JwtUtil.getUserInfoFromToken(token.replace("Bearer ", ""), secretKey);
            String userId = userInfo.getUserId(); // Get user ID

            // Generate a new unique request ID
            String requestId = UUID.randomUUID().toString();

            // Create DTO to send through Kafka
            SendRequestDto dto = new SendRequestDto(token, userId, requestId, task, file.getBytes());

            // Call service to send request
            comparisionRequestPort.comparisionRequest(dto);

            // Return success response with request ID
            return ResponseEntity.ok(
                new OurApiResponse<>(
                    "success",
                    requestId,
                    "업로드 성공"
                )
            );
        } catch (Exception e) {
            // Return failure response with error message
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
