package com.yourorg.grading.port.in;

import java.util.Map;

import com.yourorg.grading.adapter.in.dto.OurApiResponse;

public interface ProgressPublisherPort {
    void sendProgressToUser(String token, OurApiResponse<Map<String, Object>> progress);
    void sendResultToUser(String token, OurApiResponse<Map<String, Object>> result);
}
