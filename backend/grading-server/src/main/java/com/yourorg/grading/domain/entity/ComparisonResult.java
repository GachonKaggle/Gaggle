package com.yourorg.grading.domain.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "comparision_results")
public class ComparisonResult {
    @Id
    private String requestId;
    private String userId;
    private String token;
    private String loginId;
    private Double psnrAvg;
    private Double ssimAvg;
    private String task;
    private String days;

    public ComparisonResult(String userId, String requestId, String loginId, Double psnrAvg, Double ssimAvg, String task, String days) {
        this.userId = userId;
        this.requestId = requestId;
        this.loginId = loginId;
        this.psnrAvg = psnrAvg;
        this.ssimAvg = ssimAvg;
        this.task = task;
        this.days = days;
    }
}
