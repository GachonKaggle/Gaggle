package com.yourorg.grading.adapter.in.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yourorg.grading.port.in.TaskPublisherPort;
import com.yourorg.grading.adapter.in.dto.OurApiResponse;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskKafkaConsumer {
    
    private final ObjectMapper objectMapper;
    private final TaskPublisherPort taskPublisherPort;

    @KafkaListener(topics = "task.status", groupId = "task-status-service")
    public void consumeTaskSuccess(String message) throws Exception {
        Map<String, Object> task = objectMapper.readValue(message, Map.class);

        String token = (String) task.get("token");
        String userId = (String) task.get("userId");
        String requestId = (String) task.get("requestId");
        String loginId = (String) task.get("loginId");
        String taskName = (String) task.get("task");
        String status = (String) task.get("status");
        log.info("[Task] requestId={} loginId={} task={}", requestId, loginId, taskName);
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        // 1. OurApiResponse로 감싸서 프론트에 전달
        OurApiResponse<Map<String, Object>> response =
                new OurApiResponse<>(status, task, null);

        taskPublisherPort.sendTaskStatus(token, response);
        taskPublisherPort.saveTask(token, taskName);
    }
}
