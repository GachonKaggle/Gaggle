package com.yourorg.grading.port.out;

import java.util.Map;

import com.yourorg.grading.adapter.in.dto.OurApiResponse;

public interface TaskConsumerPort {
    void sendTaskStatus(String token, OurApiResponse<Map<String, Object>> taskResult);
}
