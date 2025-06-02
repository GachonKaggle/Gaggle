package com.yourorg.leaderboard;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yourorg.leaderboard.adapter.in.dto.JwtUserInfoDto;
import com.yourorg.leaderboard.adapter.in.dto.LeaderBoardDto;
import com.yourorg.leaderboard.port.in.LeaderBoardPort;
import com.yourorg.leaderboard.adapter.in.api.LeaderBoardApiAdapter;
import com.yourorg.leaderboard.util.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LeaderBoardApiAdapter.class)
class LeaderBoardApiAdapterTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LeaderBoardPort leaderBoardPort;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String secretKey = "testSecretKey";

    @Test
    @DisplayName("전체 유저 랭킹 조회 성공")
    void getLeaderBoard_success() throws Exception {
        // given
        String token = "Bearer test.jwt.token";
        String task = "mock";
        LeaderBoardDto dto = new LeaderBoardDto("userA", 38.9, 0.99, "mock", 1L, "2024-06-02");

        List<LeaderBoardDto> resultList = List.of(dto);

        // JWT 유틸 Mocking
        try (MockedStatic<JwtUtil> jwtUtilMockedStatic = mockStatic(JwtUtil.class)) {
            jwtUtilMockedStatic.when(() -> JwtUtil.getUserInfoFromToken(anyString(), anyString()))
                    .thenReturn(new JwtUserInfoDto("userA", "User A"));

            when(leaderBoardPort.getLeaderBoardsByTask(task)).thenReturn(resultList);

            // when & then
            mockMvc.perform(get("/api/leaderboard")
                            .header("Authorization", token)
                            .param("task", task)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("success"))
                    .andExpect(jsonPath("$.data[0].loginId").value("userA"))
                    .andExpect(jsonPath("$.data[0].psnrAvg").value(38.9))
                    .andExpect(jsonPath("$.data[0].rank").value(1));
        }
    }

    @Test
    @DisplayName("전체 유저 랭킹 데이터 없음")
    void getLeaderBoard_noData() throws Exception {
        String token = "Bearer test.jwt.token";
        String task = "mock";

        try (MockedStatic<JwtUtil> jwtUtilMockedStatic = mockStatic(JwtUtil.class)) {
            jwtUtilMockedStatic.when(() -> JwtUtil.getUserInfoFromToken(anyString(), anyString()))
                    .thenReturn(new JwtUserInfoDto("userA", "User A"));

            when(leaderBoardPort.getLeaderBoardsByTask(task)).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/leaderboard")
                            .header("Authorization", token)
                            .param("task", task)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value("fail"))
                    .andExpect(jsonPath("$.data").doesNotExist())
                    .andExpect(jsonPath("$.message").value("랭킹 데이터가 없습니다."));
        }
    }

    @Test
    @DisplayName("개별 유저 랭킹 조회 성공")
    void getLeaderBoardByUser_success() throws Exception {
        String token = "Bearer test.jwt.token";
        String userId = "userB";
        // 기대값과 일치하도록 수정
        LeaderBoardDto dto = new LeaderBoardDto("userB", 37.5, 0.99, "mock", 2L, "2024-06-02");

        List<LeaderBoardDto> resultList = List.of(dto);

        try (MockedStatic<JwtUtil> jwtUtilMockedStatic = mockStatic(JwtUtil.class)) {
            jwtUtilMockedStatic.when(() -> JwtUtil.getUserInfoFromToken(anyString(), anyString()))
                    .thenReturn(new JwtUserInfoDto(userId, "User B"));

            when(leaderBoardPort.getLeaderBoardByUser(userId)).thenReturn(resultList);

            mockMvc.perform(get("/api/leaderboard/user")
                            .header("Authorization", token)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("success"))
                    .andExpect(jsonPath("$.data[0].loginId").value("userB"))
                    .andExpect(jsonPath("$.data[0].psnrAvg").value(37.5))
                    .andExpect(jsonPath("$.data[0].rank").value(2));
        }
    }


    @Test
    @DisplayName("개별 유저 랭킹 데이터 없음")
    void getLeaderBoardByUser_noData() throws Exception {
        String token = "Bearer test.jwt.token";
        String userId = "userB";

        try (MockedStatic<JwtUtil> jwtUtilMockedStatic = mockStatic(JwtUtil.class)) {
            jwtUtilMockedStatic.when(() -> JwtUtil.getUserInfoFromToken(anyString(), anyString()))
                    .thenReturn(new JwtUserInfoDto(userId, "User B"));

            when(leaderBoardPort.getLeaderBoardByUser(userId)).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/leaderboard/user")
                            .header("Authorization", token)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value("fail"))
                    .andExpect(jsonPath("$.data").doesNotExist())
                    .andExpect(jsonPath("$.message").value("해당 유저의 랭킹 데이터가 없습니다."));
        }
    }
}
