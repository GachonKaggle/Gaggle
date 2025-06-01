package com.yourorg.leaderboard.adapter.in.dto;

import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;


@Getter
@Setter
@AllArgsConstructor
@Schema(description = "API 공통 응답")
public class OurApiResponse<T> {
    @Schema(description = "상태", example = "success, fail")
    private String status;
    @Schema(description = "데이터")
    private T data;
    @Schema(description = "응답 메시지", nullable = true)
    private String message;
}
