package com.yourorg.grading.adapter.in.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*; // For REST API handling
import org.springframework.web.multipart.MultipartFile; // For handling file uploads
import com.yourorg.grading.adapter.out.dto.SendRequestDto; // DTO to send request data
import com.yourorg.grading.adapter.in.dto.JwtUserInfoDto; // DTO containing parsed JWT user info
import com.yourorg.grading.util.JwtUtil; // Utility for handling JWT operations
import com.yourorg.grading.port.in.TaskRequestPort; // Port for task upload logic
import org.springframework.beans.factory.annotation.Value; // For reading values from application.properties

import java.util.UUID; // For generating unique requestId

import com.yourorg.grading.adapter.in.dto.OurApiResponse; // Custom API response wrapper

import io.swagger.v3.oas.annotations.Operation; // Swagger API annotation
import io.swagger.v3.oas.annotations.responses.ApiResponse; // Swagger response annotation
import io.swagger.v3.oas.annotations.responses.ApiResponses; // Swagger container for multiple responses
import io.swagger.v3.oas.annotations.media.Content; // Swagger content annotation
import io.swagger.v3.oas.annotations.media.Schema; // Swagger schema definition
import io.swagger.v3.oas.annotations.media.ExampleObject; // Swagger example response
import io.swagger.v3.oas.annotations.security.SecurityRequirement; // Swagger security requirement

@RestController  // Marks this class as a REST controller
@RequestMapping("/api/grading/task")  // Endpoint prefix for task upload API
@RequiredArgsConstructor  // Auto-generates constructor for final fields
public class TaskRequestAdapter {

    private final TaskRequestPort taskRequestPort;  // Port that handles the task upload logic

    @Value("${jwt.secret}")  // Injects the JWT secret key
    private String secretKey;

    @Operation(
        summary = "Upload grading file as ADMIN",  // Summary shown in Swagger
        description = "Only users with ADMIN role can upload grading files. On success, returns requestId as a string.",  // Detailed description
        security = @SecurityRequirement(name = "bearerAuth")  // JWT bearer token is required
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",  // Response code for success
            description = "Upload successful",  // Description shown in Swagger
            content = @Content(
                mediaType = "application/json",  // Response type
                schema = @Schema(implementation = OurApiResponse.class),  // Schema class used
                examples = @ExampleObject(  // Example JSON response
                    name = "Successful Upload Example",
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
            responseCode = "403",  // Response code for forbidden
            description = "Forbidden (non-admin access)",  // Description shown in Swagger
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = OurApiResponse.class),
                examples = @ExampleObject(
                    name = "Access Denied Example",
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
            responseCode = "500",  // Response code for internal error
            description = "Internal server error",  // Description shown in Swagger
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = OurApiResponse.class),
                examples = @ExampleObject(
                    name = "Failure Example",
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
    @PostMapping(consumes = {"multipart/form-data"})  // Accepts file upload requests
    public ResponseEntity<OurApiResponse<String>> comparison(
            @RequestHeader("Authorization") String token,  // JWT Bearer token
            @RequestParam("file") MultipartFile file,      // Uploaded file
            @RequestParam("task") String task              // Task name
    ) {
        try {
            // Parse JWT token to get user info
            JwtUserInfoDto userInfo = JwtUtil.getUserInfoFromToken(token.replace("Bearer ", ""), secretKey);
            String userId = userInfo.getUserId();  // Extract user ID
            String role = userInfo.getRole();      // Extract user role

            // If user is not ADMIN, reject the request
            if ("USER".equals(role)) {
                return ResponseEntity
                        .status(403)  // HTTP 403 Forbidden
                        .body(new OurApiResponse<>(
                            "fail",
                            null,
                            "접근 권한이 없습니다."  // Access denied (do not translate)
                        ));
            }

            // Generate unique requestId for tracking
            String requestId = UUID.randomUUID().toString();

            // Build a request DTO object with uploaded file data
            SendRequestDto dto = new SendRequestDto(
                token,
                userId,
                requestId,
                task,
                file.getBytes()
            );

            // Send the request through the task request port
            taskRequestPort.taskRequest(dto);

            // Return success response with generated requestId
            return ResponseEntity.ok(
                new OurApiResponse<>(
                    "success",
                    requestId,
                    "업로드 성공"  // Upload success (do not translate)
                )
            );
        } catch (Exception e) {
            // Return failure response with exception message
            return ResponseEntity.internalServerError().body(
                new OurApiResponse<>(
                    "fail",
                    null,
                    "처리 실패: " + e.getMessage()  // Processing failed (do not translate)
                )
            );
        }
    }
}
