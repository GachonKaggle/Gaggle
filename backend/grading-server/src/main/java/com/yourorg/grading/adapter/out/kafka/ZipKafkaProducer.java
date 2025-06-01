package com.yourorg.grading.adapter.out.kafka;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import com.yourorg.grading.adapter.out.dto.SendRequestDto;
import com.yourorg.grading.adapter.out.repository.UserRepository;
import com.yourorg.grading.port.out.ComparisonProducerPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yourorg.grading.domain.entity.User;
import java.util.Optional;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class ZipKafkaProducer implements ComparisonProducerPort {

    private final UserRepository userRepository;
    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    private static final String CompareTOPIC = "compare.zip";
    private static final String TaskCompareTOPIC = "task.compare.zip";
    private final ObjectMapper objectMapper;

    @Override
    public void sendZipFile(SendRequestDto dto) {

        Optional<User> newdtoOpt = userRepository.findById(dto.getUserId());
        if (newdtoOpt.isPresent()) {
            User newdto = newdtoOpt.get();
            dto.setLoginId(newdto.getLoginId());
        }

        try {
        String key = UUID.randomUUID().toString();
        String payload = objectMapper.writeValueAsString(dto);
        byte[] payloadBytes = payload.getBytes(StandardCharsets.UTF_8);
        kafkaTemplate.send(CompareTOPIC, key, payloadBytes)
            .whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("✅ [Kafka] 전송 성공 | Key: {}, Partition: {}",
                            key, result.getRecordMetadata().partition());
                } else {
                    log.error("❌ [Kafka] 전송 실패 | Key: {} | Error: {}",
                            key, ex.getMessage());
                }
        });
    }catch (Exception e) {
        log.error("메시지 처리 실패: {}", e.getMessage());
        }
    }

    @Override
    public void sendTaskZipFile(SendRequestDto dto) {
        Optional<User> newdtoOpt = userRepository.findById(dto.getUserId());
        if (newdtoOpt.isPresent()) {
            User newdto = newdtoOpt.get();
            dto.setLoginId(newdto.getLoginId());
        }
        try {
            String key = UUID.randomUUID().toString();
            String payload = objectMapper.writeValueAsString(dto);
            byte[] payloadBytes = payload.getBytes(StandardCharsets.UTF_8);
            kafkaTemplate.send(TaskCompareTOPIC, key, payloadBytes)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("✅ [Kafka] task 전송 성공 | Key: {}, Partition: {}",
                                key, result.getRecordMetadata().partition());
                    } else {
                        log.error("❌ [Kafka] 전송 실패 | Key: {} | Error: {}",
                                key, ex.getMessage());
                    }
            });
        }catch (Exception e) {
            log.error("메시지 처리 실패: {}", e.getMessage());
            }
    }
}
