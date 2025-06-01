package com.yourorg.leaderboard.adapter.in.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class JwtUserInfoDto {
    private String userId;
    private String role;
}
