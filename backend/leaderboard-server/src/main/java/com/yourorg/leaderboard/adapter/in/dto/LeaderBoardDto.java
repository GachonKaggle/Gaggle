package com.yourorg.leaderboard.adapter.in.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import com.yourorg.leaderboard.domain.entity.LeaderBoard;

@Getter
@Setter
@RequiredArgsConstructor 
@AllArgsConstructor 
public class LeaderBoardDto {


    private String loginId;
    private Double psnrAvg;
    private Double ssimAvg;
    private String task;
    private Long rank;
    private String days;

    public static LeaderBoardDto fromEntity(LeaderBoard leaderboard) {
        LeaderBoardDto dto = new LeaderBoardDto();
        dto.setLoginId(leaderboard.getLoginId());
        dto.setPsnrAvg(leaderboard.getPsnrAvg());
        dto.setSsimAvg(leaderboard.getSsimAvg());
        dto.setTask(leaderboard.getTask());
        dto.setRank(leaderboard.getRank());
        dto.setDays(leaderboard.getDays());
        return dto;
    }
}
