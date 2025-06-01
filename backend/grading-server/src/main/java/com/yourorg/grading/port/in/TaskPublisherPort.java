package com.yourorg.grading.port.in;

import java.util.Map;

import com.yourorg.grading.adapter.in.dto.OurApiResponse;

public interface TaskPublisherPort {
    void sendTaskStatus(String token, OurApiResponse<Map<String, Object>> taskResult);
    void saveTask(String token, String taskName);
}
