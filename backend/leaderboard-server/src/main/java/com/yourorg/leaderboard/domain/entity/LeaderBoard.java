package com.yourorg.leaderboard.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.NoArgsConstructor;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "comparision_results")
public class LeaderBoard {
    @Id
    private String requestId;
    private String userId;
    private String token;
    private String loginId;
    private Double psnrAvg;
    private Double ssimAvg;
    private String task;
    private String days;
    private Long rank;

    public LeaderBoard(String userId, String requestId, String loginId, Double psnrAvg, Double ssimAvg, String task, String days, Long rank) {
        this.userId = userId;
        this.requestId = requestId;
        this.loginId = loginId;
        this.psnrAvg = psnrAvg;
        this.ssimAvg = ssimAvg;
        this.task = task;
        this.days = days;
        this.rank = rank;
    }
}