package com.yourorg.grading.adapter.out.websocket;

import com.yourorg.grading.adapter.in.dto.OurApiResponse;
import com.yourorg.grading.port.out.ComparisonConsumerPort;
import com.yourorg.grading.port.out.TaskConsumerPort;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class WebSocketProgressPublisher implements ComparisonConsumerPort, TaskConsumerPort {
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void sendProgressToUser(String token, OurApiResponse<Map<String, Object>> progress) {
        messagingTemplate.convertAndSend("/topic/progress/" + token, progress);
    }

    @Override
    public void sendResultToUser(String token, OurApiResponse<Map<String, Object>> result) {
        messagingTemplate.convertAndSend("/topic/result/" + token, result);
    }

    @Override
    public void sendTaskStatus(String token, OurApiResponse<Map<String, Object>> taskResult) {
        messagingTemplate.convertAndSend("/topic/task/" + token, taskResult);
    }
}
