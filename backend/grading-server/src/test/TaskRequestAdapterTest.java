// src/test/java/com/yourorg/grading/adapter/in/web/TaskRequestAdapterTest.java

package com.yourorg.grading.adapter.in.web;

import com.yourorg.grading.port.in.TaskRequestPort;
import com.yourorg.grading.adapter.in.dto.JwtUserInfoDto;
import com.yourorg.grading.util.JwtUtil;
import com.yourorg.grading.adapter.in.dto.OurApiResponse;
import com.yourorg.grading.adapter.out.dto.SendRequestDto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

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
class TaskRequestAdapterTest {

    @Mock
    private TaskRequestPort taskRequestPort;

    @InjectMocks
    private TaskRequestAdapter adapter;

    private final String secretKey = "some-secret";
    private final String testToken = "Bearer test-token";
    private final String userId = "admin-123";
    private final String loginId = "admin-login";

    @BeforeEach
    void setup() throws Exception {
        // JWT 파싱 normal: 관리자
        Mockito.mockStatic(JwtUtil.class)
            .when(() -> JwtUtil.getUserInfoFromToken(anyString(), anyString()))
            .thenReturn(new JwtUserInfoDto(userId, loginId, "ADMIN"));
    }

    @Test
    void 관리자_업로드_성공시_정상_응답() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "admin.zip", "application/zip", "adminfile".getBytes());

        ResponseEntity<OurApiResponse<String>> response = adapter.comparison(
                testToken, file, "adminTask");

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        OurApiResponse<String> body = response.getBody();
        assertThat(body.getStatus()).isEqualTo("success");
        assertThat(body.getData()).isNotNull();
        assertThat(body.getMessage()).isEqualTo("업로드 성공");
        verify(taskRequestPort, times(1)).taskRequest(any(SendRequestDto.class));
    }

    @Test
    void USER_권한이면_403_리턴() throws Exception {
        // JWT 파싱 후 role을 USER로 돌려주기
        Mockito.mockStatic(JwtUtil.class)
            .when(() -> JwtUtil.getUserInfoFromToken(anyString(), anyString()))
            .thenReturn(new JwtUserInfoDto(userId, loginId, "USER"));

        MockMultipartFile file = new MockMultipartFile("file", "userfile.zip", "application/zip", "userfile".getBytes());

        ResponseEntity<OurApiResponse<String>> response = adapter.comparison(
                testToken, file, "userTask");

        assertThat(response.getStatusCodeValue()).isEqualTo(403);
        OurApiResponse<String> body = response.getBody();
        assertThat(body.getStatus()).isEqualTo("fail");
        assertThat(body.getData()).isNull();
        assertThat(body.getMessage()).contains("접근 권한이 없습니다.");
        verify(taskRequestPort, times(0)).taskRequest(any());
    }

    @Test
    void 예외_발생시_500과_fail_메시지_반환() throws Exception {
        // JWT 정상, 포트 에러
        doThrow(new RuntimeException("File error")).when(taskRequestPort).taskRequest(any(SendRequestDto.class));
        MockMultipartFile file = new MockMultipartFile("file", "test.zip", "application/zip", "testdata".getBytes());

        ResponseEntity<OurApiResponse<String>> response = adapter.comparison(
                testToken, file, "someTask");

        assertThat(response.getStatusCodeValue()).isEqualTo(500);
        OurApiResponse<String> body = response.getBody();
        assertThat(body.getStatus()).isEqualTo("fail");
        assertThat(body.getData()).isNull();
        assertThat(body.getMessage()).contains("처리 실패");
    }
}
