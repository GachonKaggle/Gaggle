package com.yourorg.user_info.adapter.in.auth;

import com.yourorg.user_info.domain.entity.User;
import com.yourorg.user_info.port.in.auth.AuthPort;
import com.yourorg.user_info.adapter.in.dto.LoginRequestdto;
import com.yourorg.user_info.adapter.in.dto.SignupRequestdto;
import com.yourorg.user_info.adapter.in.dto.LoginResponsedto;
import com.yourorg.user_info.adapter.in.dto.OurApiResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;

@RestController
@RequestMapping("/api/user-info")
@RequiredArgsConstructor
public class AuthAdapter {

    private final AuthPort authPort;

    @Operation(
        summary = "회원가입",
        description = "신규 유저를 회원가입시킵니다.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(
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
        @ApiResponse(
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
    @PostMapping("/signup")
    public ResponseEntity<OurApiResponse<User>> signup(@RequestBody SignupRequestdto req) {
        try {
            User user = authPort.signup(req.getLoginId(), req.getPassword(), req.getRole());
            return ResponseEntity.ok(
                new OurApiResponse<>("success", user, "회원가입 성공")
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new OurApiResponse<>("fail", null, e.getMessage())
            );
        }
    }

    @Operation(
        summary = "로그인",
        description = "로그인 시도 후 토큰 및 유저 정보를 반환합니다.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(
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
        @ApiResponse(
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
    @PostMapping("/login")
    public ResponseEntity<OurApiResponse<LoginResponsedto>> login(@RequestBody LoginRequestdto req) {
        try {
            LoginResponsedto userResponse = authPort.login(req.getLoginId(), req.getPassword());
            return ResponseEntity.ok(
                new OurApiResponse<>("success", userResponse, "로그인 성공")
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new OurApiResponse<>("fail", null, e.getMessage())
            );
        }
    }
}
