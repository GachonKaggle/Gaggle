package com.yourorg.user_info.adapter.in.auth; // Define package for user authentication API adapter

import com.yourorg.user_info.domain.entity.User; // Import User entity
import com.yourorg.user_info.port.in.auth.AuthPort; // Import authentication use case port
import com.yourorg.user_info.adapter.in.dto.LoginRequestdto; // Import login request DTO
import com.yourorg.user_info.adapter.in.dto.SignupRequestdto; // Import signup request DTO
import com.yourorg.user_info.adapter.in.dto.LoginResponsedto; // Import login response DTO
import com.yourorg.user_info.adapter.in.dto.OurApiResponse; // Import standard API response DTO

import lombok.RequiredArgsConstructor; // Lombok annotation to generate required constructor
import org.springframework.http.ResponseEntity; // For building HTTP responses
import org.springframework.web.bind.annotation.*; // Import for controller and mapping annotations

import io.swagger.v3.oas.annotations.Operation; // Swagger annotation for endpoint metadata
import io.swagger.v3.oas.annotations.responses.ApiResponse; // Swagger annotation for response descriptions
import io.swagger.v3.oas.annotations.responses.ApiResponses; // Swagger annotation to group responses
import io.swagger.v3.oas.annotations.security.SecurityRequirement; // Swagger annotation for security
import io.swagger.v3.oas.annotations.media.Content; // Swagger annotation for content metadata
import io.swagger.v3.oas.annotations.media.Schema; // Swagger annotation for schema details
import io.swagger.v3.oas.annotations.media.ExampleObject; // Swagger annotation for example payloads

@RestController // Marks this class as a REST controller
@RequestMapping("/api/user-info") // Base URL mapping for user info endpoints
@RequiredArgsConstructor // Lombok: generate constructor for final fields
public class AuthAdapter { // Controller class for authentication features

    private final AuthPort authPort; // Port interface for authentication business logic

    @Operation( // Swagger operation metadata
        summary = "회원가입", // API summary (in Korean)
        description = "신규 유저를 회원가입시킵니다.", // API description (in Korean)
        security = @SecurityRequirement(name = "bearerAuth") // Require JWT auth
    )
    @ApiResponses({ // Define possible API responses
        @ApiResponse( // Successful signup example
            responseCode = "200",
            description = "회원가입 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = OurApiResponse.class),
                examples = @ExampleObject(
                    name = "회원가입 성공 예시",
                    value = """
                    {
                      "status": "success",
                      "data": {
                        "loginId": "user123"
                      },
                      "message": "회원가입 성공"
                    }
                    """
                )
            )
        ),
        @ApiResponse( // Signup failure example
            responseCode = "400",
            description = "입력값 오류 등 실패 케이스",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = OurApiResponse.class),
                examples = @ExampleObject(
                    name = "회원가입 실패 예시",
                    value = """
                    {
                      "status": "fail",
                      "data": null,
                      "message": "이미 존재하는 아이디입니다."
                    }
                    """
                )
            )
        )
    })
    @PostMapping("/signup") // Maps to POST /api/user-info/signup
    public ResponseEntity<OurApiResponse<User>> signup(@RequestBody SignupRequestdto req) { // Signup handler method
        try {
            User user = authPort.signup(req.getLoginId(), req.getPassword(), req.getRole()); // Call signup use case
            return ResponseEntity.ok( // Return successful response
                new OurApiResponse<>("success", user, "회원가입 성공")
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body( // Return failure response
                new OurApiResponse<>("fail", null, e.getMessage())
            );
        }
    }

    @Operation( // Swagger operation for login
        summary = "로그인", // Summary in Korean
        description = "로그인 시도 후 토큰 및 유저 정보를 반환합니다.", // Description in Korean
        security = @SecurityRequirement(name = "bearerAuth") // JWT auth required
    )
    @ApiResponses({ // Define login response types
        @ApiResponse( // Successful login example
            responseCode = "200",
            description = "로그인 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = OurApiResponse.class),
                examples = @ExampleObject(
                    name = "로그인 성공 예시",
                    value = """
                    {
                      "status": "success",
                      "data": {
                        "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
                      },
                      "message": "로그인 성공"
                    }
                    """
                )
            )
        ),
        @ApiResponse( // Failed login example
            responseCode = "400",
            description = "로그인 실패",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = OurApiResponse.class),
                examples = @ExampleObject(
                    name = "로그인 실패 예시",
                    value = """
                    {
                      "status": "fail",
                      "data": null,
                      "message": "로그인 정보가 일치하지 않습니다."
                    }
                    """
                )
            )
        )
    })
    @PostMapping("/login") // Maps to POST /api/user-info/login
    public ResponseEntity<OurApiResponse<LoginResponsedto>> login(@RequestBody LoginRequestdto req) { // Login handler method
        try {
            LoginResponsedto userResponse = authPort.login(req.getLoginId(), req.getPassword()); // Call login use case
            return ResponseEntity.ok( // Return success response
                new OurApiResponse<>("success", userResponse, "로그인 성공")
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body( // Return failure response
                new OurApiResponse<>("fail", null, e.getMessage())
            );
        }
    }
}
