package com.yourorg.grading.adapter.out.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendRequestDto {
    private String token;
    private String userId;
    private String loginId;
    private String requestId;
    private String task;
    private byte[] zipFile;

    public SendRequestDto(String token, String userId, String requestId, String task, byte[] zipFile) {
        this.token = token;
        this.userId = userId;
        this.requestId = requestId;
        this.task = task;
        this.zipFile = zipFile;
    }
}
