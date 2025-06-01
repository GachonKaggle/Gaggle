package com.yourorg.grading.adapter.in.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yourorg.grading.port.in.TaskPublisherPort;
import com.yourorg.grading.adapter.in.dto.OurApiResponse;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j  // Enables logging using SLF4J (e.g., log.info())
@Component  // Marks this class as a Spring Bean (for component scanning)
@RequiredArgsConstructor  // Generates a constructor for all final fields
public class TaskKafkaConsumer {
    
    private final ObjectMapper objectMapper;  // Used to deserialize JSON strings
    private final TaskPublisherPort taskPublisherPort;  // Port for publishing task status to WebSocket or saving

    // Kafka listener for messages from the 'task.status' topic
    @KafkaListener(topics = "task.status", groupId = "task-status-service")
    public void consumeTaskSuccess(String message) throws Exception {
        // Deserialize the incoming JSON message to a Map
        Map<String, Object> task = objectMapper.readValue(message, Map.class);

        // Extract necessary fields from the message
        String token = (String) task.get("token");
        String userId = (String) task.get("userId");
        String requestId = (String) task.get("requestId");
        String loginId = (String) task.get("loginId");
        String taskName = (String) task.get("task");
        String status = (String) task.get("status");

        // Log the received task info
        log.info("[Task] requestId={} loginId={} task={}", requestId, loginId, taskName);

        // Remove "Bearer " prefix from token if present
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 1. Wrap the task info in a standardized API response format
        OurApiResponse<Map<String, Object>> response =
                new OurApiResponse<>(status, task, null);

        // 2. Send task status update to the frontend via WebSocket
        taskPublisherPort.sendTaskStatus(token, response);

        // 3. Persist the task info (could be used for logs, history, etc.)
        taskPublisherPort.saveTask(token, taskName);
    }
}
