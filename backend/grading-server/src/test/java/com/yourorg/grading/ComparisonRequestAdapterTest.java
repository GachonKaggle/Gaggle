package com.yourorg.grading;

import com.yourorg.grading.adapter.in.web.ComparisonRequestAdapter;
import com.yourorg.grading.port.in.ComparisonPort;
import com.yourorg.grading.adapter.in.dto.JwtUserInfoDto;
import com.yourorg.grading.util.JwtUtil;
import com.yourorg.grading.adapter.in.dto.OurApiResponse;
import com.yourorg.grading.adapter.out.dto.SendRequestDto;

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

import java.nio.file.Files;
import java.nio.file.Paths;

@ExtendWith(MockitoExtension.class)
class ComparisonRequestAdapterTest {

    @Mock
    private ComparisonPort comparisonRequestPort;

    @InjectMocks
    private ComparisonRequestAdapter adapter;

    private final String secretKey = "some-secret";
    private final String testToken = "Bearer test-token";
    private final String userId = "user-123";
    private final String role = "USER";

    private MockMultipartFile getTestZipFile() throws Exception {
        // Reads src/test/resources/result.zip (make sure this file exists)
        byte[] zipBytes = Files.readAllBytes(Paths.get("src/test/resources/result.zip"));
        return new MockMultipartFile("file", "result.zip", "application/zip", zipBytes);
    }

    @Test
    void shouldReturnSuccessResponseOnUpload() throws Exception {
        try (var jwtUtilMock = Mockito.mockStatic(JwtUtil.class)) {
            // Mock JWT parsing
            jwtUtilMock.when(() -> JwtUtil.getUserInfoFromToken(anyString(), anyString()))
                .thenReturn(new JwtUserInfoDto(userId, role));

            MockMultipartFile file = getTestZipFile();

            // No need to stub comparisonRequestPort.comparisionRequest for success (void method)

            ResponseEntity<OurApiResponse<String>> response = adapter.comparison(
                    testToken, file, "sampleTask");

            assertThat(response.getStatusCodeValue()).isEqualTo(200);
            OurApiResponse<String> body = response.getBody();
            assertThat(body.getStatus()).isEqualTo("success");
            assertThat(body.getData()).isNotNull();
            assertThat(body.getMessage()).isEqualTo("업로드 성공");

            verify(comparisonRequestPort, times(1)).comparisionRequest(any(SendRequestDto.class));
        }
    }

    @Test
    void shouldReturn500AndFailMessageOnException() throws Exception {
        try (var jwtUtilMock = Mockito.mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.getUserInfoFromToken(anyString(), anyString()))
                .thenReturn(new JwtUserInfoDto(userId, role));

            // Simulate exception in port
            doThrow(new RuntimeException("Server error"))
                .when(comparisonRequestPort).comparisionRequest(any(SendRequestDto.class));

            MockMultipartFile file = getTestZipFile();

            ResponseEntity<OurApiResponse<String>> response = adapter.comparison(
                    testToken, file, "sampleTask");

            assertThat(response.getStatusCodeValue()).isEqualTo(500);
            OurApiResponse<String> body = response.getBody();
            assertThat(body.getStatus()).isEqualTo("fail");
            assertThat(body.getData()).isNull();
            assertThat(body.getMessage()).contains("처리 실패");
        }
    }
}
