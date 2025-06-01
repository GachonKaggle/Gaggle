package com.yourorg.grading.adapter.out.kafka;

import lombok.extern.slf4j.Slf4j; // Lombok annotation for logging
import lombok.RequiredArgsConstructor; // Lombok annotation for required constructor

import org.springframework.kafka.core.KafkaTemplate; // Spring Kafka template for sending messages
import org.springframework.stereotype.Component; // Marks this as a Spring-managed component
import com.yourorg.grading.adapter.out.dto.SendRequestDto; // DTO for sending ZIP file info
import com.yourorg.grading.adapter.out.repository.UserRepository; // User repository
import com.yourorg.grading.port.out.ComparisonProducerPort; // Interface for Kafka producer port
import com.fasterxml.jackson.databind.ObjectMapper; // JSON serializer/deserializer
import com.yourorg.grading.domain.entity.User; // User entity

import java.util.Optional; // For handling optional return from repository
import java.nio.charset.StandardCharsets; // For specifying UTF-8 encoding
import java.util.UUID; // For generating unique message keys

@Component // Register as a Spring bean
@Slf4j // Enable logging
@RequiredArgsConstructor // Automatically inject final fields via constructor
public class ZipKafkaProducer implements ComparisonProducerPort {

    private final UserRepository userRepository; // Repository to fetch user loginId
    private final KafkaTemplate<String, byte[]> kafkaTemplate; // Kafka producer
    private static final String CompareTOPIC = "compare.zip"; // Topic for normal grading requests
    private static final String TaskCompareTOPIC = "task.compare.zip"; // Topic for admin grading tasks
    private final ObjectMapper objectMapper; // Used to convert DTO to JSON string

    // Sends a ZIP file message to the normal comparison topic
    @Override
    public void sendZipFile(SendRequestDto dto) {

        // Try to find the user in the DB and populate loginId
        Optional<User> newdtoOpt = userRepository.findById(dto.getUserId());
        if (newdtoOpt.isPresent()) {
            User newdto = newdtoOpt.get(); // Extract user entity
            dto.setLoginId(newdto.getLoginId()); // Set loginId to DTO
        }

        try {
            String key = UUID.randomUUID().toString(); // Generate unique Kafka key
            String payload = objectMapper.writeValueAsString(dto); // Convert DTO to JSON string
            byte[] payloadBytes = payload.getBytes(StandardCharsets.UTF_8); // Convert JSON to byte array

            // Send message to Kafka
            kafkaTemplate.send(CompareTOPIC, key, payloadBytes)
                .whenComplete((result, ex) -> { // Completion callback
                    if (ex == null) {
                        log.info("✅ [Kafka] 전송 성공 | Key: {}, Partition: {}",
                                key, result.getRecordMetadata().partition()); // Log success
                    } else {
                        log.error("❌ [Kafka] 전송 실패 | Key: {} | Error: {}",
                                key, ex.getMessage()); // Log failure
                    }
                });

        } catch (Exception e) {
            log.error("메시지 처리 실패: {}", e.getMessage()); // Log error during serialization or sending
        }
    }

    // Sends a ZIP file message to the admin-specific task topic
    @Override
    public void sendTaskZipFile(SendRequestDto dto) {

        // Try to find the user in the DB and populate loginId
        Optional<User> newdtoOpt = userRepository.findById(dto.getUserId());
        if (newdtoOpt.isPresent()) {
            User newdto = newdtoOpt.get(); // Extract user entity
            dto.setLoginId(newdto.getLoginId()); // Set loginId to DTO
        }

        try {
            String key = UUID.randomUUID().toString(); // Generate unique Kafka key
            String payload = objectMapper.writeValueAsString(dto); // Convert DTO to JSON string
            byte[] payloadBytes = payload.getBytes(StandardCharsets.UTF_8); // Convert JSON to byte array

            // Send message to Kafka
            kafkaTemplate.send(TaskCompareTOPIC, key, payloadBytes)
                .whenComplete((result, ex) -> { // Completion callback
                    if (ex == null) {
                        log.info("✅ [Kafka] task 전송 성공 | Key: {}, Partition: {}",
                                key, result.getRecordMetadata().partition()); // Log success
                    } else {
                        log.error("❌ [Kafka] 전송 실패 | Key: {} | Error: {}",
                                key, ex.getMessage()); // Log failure
                    }
                });

        } catch (Exception e) {
            log.error("메시지 처리 실패: {}", e.getMessage()); // Log error during serialization or sending
        }
    }
}
