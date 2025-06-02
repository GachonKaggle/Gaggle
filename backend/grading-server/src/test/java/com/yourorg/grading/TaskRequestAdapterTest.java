package com.yourorg.grading;

import com.yourorg.grading.port.in.TaskRequestPort;
import com.yourorg.grading.adapter.in.dto.JwtUserInfoDto;
import com.yourorg.grading.util.JwtUtil;
import com.yourorg.grading.adapter.out.dto.SendRequestDto;
import com.yourorg.grading.adapter.in.web.TaskRequestAdapter;
import com.yourorg.grading.adapter.in.dto.OurApiResponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskRequestAdapterTest {

    @Mock
    private TaskRequestPort taskRequestPort;

    @InjectMocks
    private TaskRequestAdapter adapter;

    private final String testToken = "Bearer test-token";
    private final String userId = "admin-123";
    private final String secretKey = "some-secret";

    @Test
    void adminUpload_shouldReturnSuccessAndInvokeTaskRequest() throws Exception {
        try (var jwtUtilMock = Mockito.mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.getUserInfoFromToken(anyString(), anyString()))
                .thenReturn(new JwtUserInfoDto(userId, "ADMIN"));

            MockMultipartFile file = new MockMultipartFile(
                "file", "admin.zip", "application/zip", "adminfile".getBytes()
            );

            ResponseEntity<OurApiResponse<String>> response = adapter.comparison(testToken, file, "adminTask");

            assertThat(response.getStatusCodeValue()).isEqualTo(200);
            OurApiResponse<String> body = response.getBody();
            assertThat(body.getStatus()).isEqualTo("success");
            assertThat(body.getData()).isNotNull();
            assertThat(body.getMessage()).isEqualTo("업로드 성공");

            verify(taskRequestPort, times(1)).taskRequest(any(SendRequestDto.class));
        }
    }

    @Test
    void userRole_shouldReturn403AndNotInvokeTaskRequest() throws Exception {
        try (var jwtUtilMock = Mockito.mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.getUserInfoFromToken(anyString(), anyString()))
                .thenReturn(new JwtUserInfoDto(userId, "USER"));

            MockMultipartFile file = new MockMultipartFile(
                "file", "userfile.zip", "application/zip", "userfile".getBytes()
            );

            ResponseEntity<OurApiResponse<String>> response = adapter.comparison(testToken, file, "userTask");

            assertThat(response.getStatusCodeValue()).isEqualTo(403);
            OurApiResponse<String> body = response.getBody();
            assertThat(body.getStatus()).isEqualTo("fail");
            assertThat(body.getData()).isNull();
            assertThat(body.getMessage()).isEqualTo("접근 권한이 없습니다.");

            verify(taskRequestPort, never()).taskRequest(any());
        }
    }

    @Test
    void exceptionThrown_shouldReturn500AndFailMessage() throws Exception {
        try (var jwtUtilMock = Mockito.mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.getUserInfoFromToken(anyString(), anyString()))
                .thenReturn(new JwtUserInfoDto(userId, "ADMIN"));

            doThrow(new RuntimeException("File error")).when(taskRequestPort).taskRequest(any(SendRequestDto.class));

            MockMultipartFile file = new MockMultipartFile(
                "file", "test.zip", "application/zip", "testdata".getBytes()
            );

            ResponseEntity<OurApiResponse<String>> response = adapter.comparison(testToken, file, "someTask");

            assertThat(response.getStatusCodeValue()).isEqualTo(500);
            OurApiResponse<String> body = response.getBody();
            assertThat(body.getStatus()).isEqualTo("fail");
            assertThat(body.getData()).isNull();
            assertThat(body.getMessage()).contains("처리 실패");

            verify(taskRequestPort, times(1)).taskRequest(any(SendRequestDto.class));
        }
    }
}
