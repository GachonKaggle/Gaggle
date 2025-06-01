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

import java.util.Map;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ComparisonService implements ComparisonPort, ComparisonSavePort, ProgressPublisherPort, TaskRequestPort, TaskPublisherPort {

    private final ComparisonProducerPort zipKafkaProducer;
    private final ComparisonConsumerPort comparisionConsumerPort;
    private final ComparisonMongoPort comparisionMongoPort;
    private final TaskConsumerPort taskConsumerPort;

    // 1. 업로드 처리
    @Override
    public void comparisionRequest(SendRequestDto dto) {
        zipKafkaProducer.sendZipFile(dto);
    }

    // 2. 결과 DB 저장
    @Override
    public void saveResult(String userId ,String requestId, String loginId, Double psnrAvg, Double ssimAvg, String task) {
        comparisionMongoPort.saveResultJPA(userId, requestId, loginId, psnrAvg, ssimAvg, task);
    }

    // 3. 진행 상황 userId 기준 push (Socket 등)
    @Override
    public void sendProgressToUser(String token, OurApiResponse<Map<String, Object>> progressDto) {
        comparisionConsumerPort.sendProgressToUser(token, progressDto);
    }

    // 4. 결과 userId 기준 push (Socket 등)
    @Override
    public void sendResultToUser(String token, OurApiResponse<Map<String, Object>> resultDto) {
        comparisionConsumerPort.sendResultToUser(token, resultDto);
    }

    // (업로드와 동일한 로직이 필요하다면 taskRequest도 동일하게 zipKafkaProducer로 전송)
    @Override
    public void taskRequest(SendRequestDto dto) {
        zipKafkaProducer.sendTaskZipFile(dto);
    }

    @Override
    public void sendTaskStatus(String token, OurApiResponse<Map<String, Object>> taskResult) {
        taskConsumerPort.sendTaskStatus(token, taskResult);
    }

    @Override
    public void saveTask(String token, String taskName) {
        comparisionMongoPort.saveTaskRepository(token, taskName);
    }
}
