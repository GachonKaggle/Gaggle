// src/test/java/com/yourorg/grading/adapter/in/web/ComparisonRequestAdapterTest.java

package com.yourorg.grading.adapter.in.web;

import com.yourorg.grading.port.in.ComparisonPort;
import com.yourorg.grading.adapter.in.dto.JwtUserInfoDto;
import com.yourorg.grading.util.JwtUtil;
import com.yourorg.grading.adapter.in.dto.OurApiResponse;
import com.yourorg.grading.adapter.out.dto.SendRequestDto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComparisonRequestAdapterTest {

    @Mock
    private ComparisonPort comparisionRequestPort;

    @InjectMocks
    private ComparisonRequestAdapter adapter;

    private final String secretKey = "some-secret";
    private final String testToken = "Bearer test-token";
    private final String userId = "user-123";
    private final String loginId = "login-123";

    @BeforeEach
    void setup() throws Exception {
        // 정상 JWT 파싱 Mock
        Mockito.mockStatic(JwtUtil.class)
            .when(() -> JwtUtil.getUserInfoFromToken(anyString(), anyString()))
            .thenReturn(new JwtUserInfoDto(userId, loginId, "USER"));
    }

    @Test
    void 업로드_성공시_정상_응답() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "abc.zip", "application/zip", "hello".getBytes());

        ResponseEntity<OurApiResponse<String>> response = adapter.comparison(
                testToken, file, "sampleTask");

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        OurApiResponse<String> body = response.getBody();
        assertThat(body.getStatus()).isEqualTo("success");
        assertThat(body.getData()).isNotNull();
        assertThat(body.getMessage()).isEqualTo("업로드 성공");

        // 포트 메서드가 호출된 것 검증
        verify(comparisionRequestPort, times(1)).comparisionRequest(any(SendRequestDto.class));
    }

    @Test
    void 예외_발생시_500과_fail_메시지_반환() throws Exception {
        // JWT 파싱 정상 - 포트가 에러를 던짐
        doThrow(new RuntimeException("서버 에러")).when(comparisionRequestPort).comparisionRequest(any(SendRequestDto.class));
        MockMultipartFile file = new MockMultipartFile("file", "abc.zip", "application/zip", "hello".getBytes());

        ResponseEntity<OurApiResponse<String>> response = adapter.comparison(
                testToken, file, "sampleTask");

        assertThat(response.getStatusCodeValue()).isEqualTo(500);
        OurApiResponse<String> body = response.getBody();
        assertThat(body.getStatus()).isEqualTo("fail");
        assertThat(body.getData()).isNull();
        assertThat(body.getMessage()).contains("처리 실패");
    }
}
