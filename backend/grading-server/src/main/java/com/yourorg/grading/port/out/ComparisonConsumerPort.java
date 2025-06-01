package com.yourorg.grading.port.out;

import java.util.Map;

import com.yourorg.grading.adapter.in.dto.OurApiResponse;

public interface ComparisonConsumerPort {
    void sendProgressToUser(String token, OurApiResponse<Map<String, Object>> progressDto);
    void sendResultToUser(String token, OurApiResponse<Map<String, Object>> resultDto);
}
