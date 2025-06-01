package com.yourorg.grading.adapter.in.kafka;  // Package declaration

import com.fasterxml.jackson.databind.ObjectMapper;  // Jackson library for JSON parsing
import com.yourorg.grading.port.in.ProgressPublisherPort;  // Interface for pushing progress updates
import com.yourorg.grading.port.in.ComparisonSavePort;     // Interface for saving result to DB
import com.yourorg.grading.adapter.in.dto.OurApiResponse;  // Custom API response wrapper
import lombok.RequiredArgsConstructor;  // Lombok annotation for constructor injection
import lombok.extern.slf4j.Slf4j;       // Lombok annotation to enable logging
import org.springframework.kafka.annotation.KafkaListener;  // Annotation for Kafka message listening
import org.springframework.stereotype.Component;            // Spring component annotation

import java.util.Map;  // Java Map interface

@Slf4j  // Enables logging using SLF4J
@Component  // Marks this class as a Spring-managed bean 
@RequiredArgsConstructor  // Generates constructor for final fields
public class PsnrKafkaConsumer {  // Kafka consumer class for progress and result topics

    private final ObjectMapper objectMapper;  // For parsing JSON strings into Java objects
    private final ComparisonSavePort comparisionSavePort;  // Port for saving comparison result
    private final ProgressPublisherPort progressPublisherPort;  // Port for sending socket updates

    @KafkaListener(topics = "psnr.progress", groupId = "grading-service")  // Listens to "psnr.progress" topic
    public void consumeProgress(String message) throws Exception {
        Map<String, Object> progress = objectMapper.readValue(message, Map.class);  // Parse JSON message

        String token = (String) progress.get("token");  // Extract token
        String userId = (String) progress.get("userId");  // Extract user ID
        String requestId = (String) progress.get("requestId");  // Extract request ID
        String loginId = (String) progress.get("loginId");  // Extract login ID
        String task = (String) progress.get("task");  // Extract task name
        int current = progress.get("current") != null ? ((Number)progress.get("current")).intValue() : -1;  // Current progress index
        int total = progress.get("total") != null ? ((Number)progress.get("total")).intValue() : -1;  // Total file count
        String filename = (String) progress.get("filename");  // Current filename

        if (token != null && token.startsWith("Bearer ")) {  // Remove "Bearer " prefix if present
            token = token.substring(7);
        }

        log.info("[Progress] token = {}requestId={} loginId={} task={} {}/{} filename={} status={}",
                token, requestId, loginId, task, current, total, filename);  // Log progress

        // 1. Wrap the progress message into OurApiResponse
        OurApiResponse<Map<String, Object>> response =
                new OurApiResponse<>("progress", progress, null);

        // 2. Push progress update to specific user via WebSocket
        progressPublisherPort.sendProgressToUser(token, response);
    }

    @KafkaListener(topics = "psnr.results", groupId = "grading-service")  // Listens to "psnr.results" topic
    public void consumeResult(String message) throws Exception {
        Map<String, Object> result = objectMapper.readValue(message, Map.class);  // Parse JSON message

        String token = (String) result.get("token");  // Extract token
        String userId = (String) result.get("userId");  // Extract user ID
        String requestId = (String) result.get("requestId");  // Extract request ID
        String loginId = (String) result.get("loginId");  // Extract login ID
        Double psnrAvg = result.get("psnrAvg") != null ? ((Number)result.get("psnrAvg")).doubleValue() : null;  // PSNR average
        Double ssimAvg = result.get("ssimAvg") != null ? ((Number)result.get("ssimAvg")).doubleValue() : null;  // SSIM average
        String task = (String) result.get("task");  // Task name

        // 1. Save result to MongoDB via adapter
        comparisionSavePort.saveResult(userId, requestId, loginId, psnrAvg, ssimAvg, task);

        if (token != null && token.startsWith("Bearer ")) {  // Remove "Bearer " prefix if present
            token = token.substring(7);
        }

        // 2. Determine response status based on result
        String statusStr;
        String messageText = null;

        if (psnrAvg == null) {
            // Failure case
            statusStr = "fail";
            log.info("[Result][ERROR] token = {}, requestId={} loginId={} 결과 없음, 실패 반환", token, requestId, loginId);
        } else {
            // Success case
            statusStr = "success";
            log.info("[Result][DONE] token = {}, requestId={} loginId={} 평균 PSNR: {}", token, requestId, loginId, psnrAvg);
        }

        // 3. Wrap the result into OurApiResponse
        OurApiResponse<Map<String, Object>> response =
                new OurApiResponse<>(statusStr, result, messageText);

        // 4. Push result update to specific user via WebSocket
        progressPublisherPort.sendResultToUser(token, response);
    }
}
