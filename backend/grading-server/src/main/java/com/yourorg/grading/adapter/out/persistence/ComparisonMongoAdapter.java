package com.yourorg.grading.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.yourorg.grading.domain.entity.ComparisonResult;
import com.yourorg.grading.port.out.ComparisonMongoPort;
import com.yourorg.grading.adapter.out.repository.ComparisonResultRepository;
import com.yourorg.grading.adapter.out.repository.TaskMongoRepository;
import com.yourorg.grading.domain.entity.Task;

import java.time.Instant;
@Component
@RequiredArgsConstructor
public class ComparisonMongoAdapter implements ComparisonMongoPort {

    private final ComparisonResultRepository comparisionResultRepository;
    private final TaskMongoRepository taskMongoRepository;

    @Override
    public void saveResultJPA(String userId, String requestId, String loginId, Double psnrAvg, Double ssimAvg, String task) {
        String days = Instant.now().toString();
        ComparisonResult result = new ComparisonResult(userId, requestId, loginId, psnrAvg, ssimAvg, task, days);
        comparisionResultRepository.save(result);
    }

    @Override
    public void saveTaskRepository(String token, String taskName) {
        boolean exists = taskMongoRepository.existsByTaskName(taskName);
        if (!exists) {
            Task task = new Task(taskName);
            taskMongoRepository.save(task);
        }
    }
}
