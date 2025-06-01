package com.yourorg.grading.domain.service;

import com.yourorg.grading.adapter.in.dto.OurApiResponse;
import com.yourorg.grading.adapter.out.dto.SendRequestDto;
import com.yourorg.grading.port.in.ComparisonPort;
import com.yourorg.grading.port.in.ComparisonSavePort;
import com.yourorg.grading.port.in.ProgressPublisherPort;
import com.yourorg.grading.port.in.TaskPublisherPort;
import com.yourorg.grading.port.in.TaskRequestPort;
import com.yourorg.grading.port.out.ComparisonProducerPort;
import com.yourorg.grading.port.out.ComparisonMongoPort;
import com.yourorg.grading.port.out.ComparisonConsumerPort;
import com.yourorg.grading.port.out.TaskConsumerPort;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ComparisonService implements
        ComparisonPort,                // Handles comparison request logic
        ComparisonSavePort,            // Saves comparison results
        ProgressPublisherPort,         // Publishes progress updates
        TaskRequestPort,               // Handles task-based comparison requests
        TaskPublisherPort {            // Publishes task result status

    private final ComparisonProducerPort zipKafkaProducer;         // Sends zip data to Kafka
    private final ComparisonConsumerPort comparisionConsumerPort; // Sends updates to clients
    private final ComparisonMongoPort comparisionMongoPort;       // Saves data to MongoDB
    private final TaskConsumerPort taskConsumerPort;              // Sends task status to clients

    // 1. Handle comparison file upload by sending it to Kafka
    @Override
    public void comparisionRequest(SendRequestDto dto) {
        zipKafkaProducer.sendZipFile(dto);
    }

    // 2. Save comparison result to MongoDB
    @Override
    public void saveResult(String userId, String requestId, String loginId, Double psnrAvg, Double ssimAvg, String task) {
        comparisionMongoPort.saveResultJPA(userId, requestId, loginId, psnrAvg, ssimAvg, task);
    }

    // 3. Push progress update to user via WebSocket (or similar)
    @Override
    public void sendProgressToUser(String token, OurApiResponse<Map<String, Object>> progressDto) {
        comparisionConsumerPort.sendProgressToUser(token, progressDto);
    }

    // 4. Push final result to user via WebSocket (or similar)
    @Override
    public void sendResultToUser(String token, OurApiResponse<Map<String, Object>> resultDto) {
        comparisionConsumerPort.sendResultToUser(token, resultDto);
    }

    // 5. Handle task-based grading request (admin uploads) via Kafka
    @Override
    public void taskRequest(SendRequestDto dto) {
        zipKafkaProducer.sendTaskZipFile(dto);
    }

    // 6. Push task result status to user
    @Override
    public void sendTaskStatus(String token, OurApiResponse<Map<String, Object>> taskResult) {
        taskConsumerPort.sendTaskStatus(token, taskResult);
    }

    // 7. Save new task name to MongoDB if not already stored
    @Override
    public void saveTask(String token, String taskName) {
        comparisionMongoPort.saveTaskRepository(token, taskName);
    }
}
