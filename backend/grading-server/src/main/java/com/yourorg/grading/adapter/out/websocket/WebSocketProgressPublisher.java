package com.yourorg.grading.adapter.out.websocket;

import com.yourorg.grading.adapter.in.dto.OurApiResponse;
import com.yourorg.grading.port.out.ComparisonConsumerPort;
import com.yourorg.grading.port.out.TaskConsumerPort;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component  // Registers this class as a Spring Bean
@RequiredArgsConstructor  // Lombok: creates constructor for final fields
public class WebSocketProgressPublisher implements ComparisonConsumerPort, TaskConsumerPort {

    private final SimpMessagingTemplate messagingTemplate;  // Spring’s helper class for sending WebSocket messages

    // Send progress updates to the user via WebSocket
    @Override
    public void sendProgressToUser(String token, OurApiResponse<Map<String, Object>> progress) {
        messagingTemplate.convertAndSend("/topic/progress/" + token, progress);  // Sends to /topic/progress/{token}
    }

    // Send final result updates to the user via WebSocket
    @Override
    public void sendResultToUser(String token, OurApiResponse<Map<String, Object>> result) {
        messagingTemplate.convertAndSend("/topic/result/" + token, result);  // Sends to /topic/result/{token}
    }

    // Send task status (e.g., reference image upload) to the user via WebSocket
    @Override
    public void sendTaskStatus(String token, OurApiResponse<Map<String, Object>> taskResult) {
        messagingTemplate.convertAndSend("/topic/task/" + token, taskResult);  // Sends to /topic/task/{token}
    }
}
