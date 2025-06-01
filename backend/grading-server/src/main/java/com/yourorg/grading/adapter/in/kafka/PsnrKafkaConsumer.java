package com.yourorg.grading.adapter.in.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yourorg.grading.port.in.ProgressPublisherPort;
import com.yourorg.grading.port.in.ComparisonSavePort;
import com.yourorg.grading.adapter.in.dto.OurApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class PsnrKafkaConsumer {

    private final ObjectMapper objectMapper;
    private final ComparisonSavePort comparisionSavePort;
    private final ProgressPublisherPort progressPublisherPort;

    @KafkaListener(topics = "psnr.progress", groupId = "grading-service")
    public void consumeProgress(String message) throws Exception {
        Map<String, Object> progress = objectMapper.readValue(message, Map.class);

        String token = (String) progress.get("token");
        String userId = (String) progress.get("userId");
        String requestId = (String) progress.get("requestId");
        String loginId = (String) progress.get("loginId");
        String task = (String) progress.get("task");
        int current = progress.get("current") != null ? ((Number)progress.get("current")).intValue() : -1;
        int total = progress.get("total") != null ? ((Number)progress.get("total")).intValue() : -1;
        String filename = (String) progress.get("filename");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        log.info("[Progress] token = {}requestId={} loginId={} task={} {}/{} filename={} status={}",
                token, requestId, loginId, task, current, total, filename);

        // 1. OurApiResponse로 감싸서 프론트에 전달
        OurApiResponse<Map<String, Object>> response =
                new OurApiResponse<>("progress", progress, null);

        // 2. 소켓으로 해당 유저(loginId)에게만 즉시 push!
        progressPublisherPort.sendProgressToUser(token, response);
    }

    @KafkaListener(topics = "psnr.results", groupId = "grading-service")
    public void consumeResult(String message) throws Exception {
        Map<String, Object> result = objectMapper.readValue(message, Map.class);

        String token = (String) result.get("token");
        String userId = (String) result.get("userId");
        String requestId = (String) result.get("requestId");
        String loginId = (String) result.get("loginId");
        Double psnrAvg = result.get("psnrAvg") != null ? ((Number)result.get("psnrAvg")).doubleValue() : null;
        Double ssimAvg = result.get("ssimAvg") != null ? ((Number)result.get("ssimAvg")).doubleValue() : null;
        String task = (String) result.get("task");
        // 1. DB 저장
        comparisionSavePort.saveResult(userId, requestId, loginId, psnrAvg, ssimAvg, task);
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        // 2. OurApiResponse 래핑용 status/message
        String statusStr;
        String messageText = null;

        if (psnrAvg == null) {
            // 실패 메시지
            statusStr = "fail";
            log.info("[Result][ERROR] token = {}, requestId={} loginId={} 결과 없음, 실패 반환", token, requestId, loginId);
        } else {
            // 성공
            statusStr = "success";
            log.info("[Result][DONE] token = {}, requestId={} loginId={} 평균 PSNR: {}", token, requestId, loginId, psnrAvg);
        }

        // 3. OurApiResponse로 감싸서 프론트에 전송
        OurApiResponse<Map<String, Object>> response =
                new OurApiResponse<>(statusStr, result, messageText);

        // 4. 소켓으로 해당 유저(loginId)에게만 push
        progressPublisherPort.sendResultToUser(token, response);
    }
}
