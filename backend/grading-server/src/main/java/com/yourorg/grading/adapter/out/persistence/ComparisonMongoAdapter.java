package com.yourorg.grading.adapter.out.persistence;

import lombok.RequiredArgsConstructor; // Lombok for constructor injection
import org.springframework.stereotype.Component; // Marks this class as a Spring component
import com.yourorg.grading.domain.entity.ComparisonResult; // Domain entity for comparison result
import com.yourorg.grading.port.out.ComparisonMongoPort; // Output port interface for saving comparison result
import com.yourorg.grading.adapter.out.repository.ComparisonResultRepository; // Repository for saving result
import com.yourorg.grading.adapter.out.repository.TaskMongoRepository; // Repository for saving tasks
import com.yourorg.grading.domain.entity.Task; // Domain entity for task

import java.time.Instant; // Used to get current timestamp

@Component // Register this class as a Spring component
@RequiredArgsConstructor // Automatically generate constructor for final fields
public class ComparisonMongoAdapter implements ComparisonMongoPort {

    private final ComparisonResultRepository comparisionResultRepository; // Repository to store comparison results
    private final TaskMongoRepository taskMongoRepository; // Repository to store tasks

    // Save the result to MongoDB
    @Override
    public void saveResultJPA(String userId, String requestId, String loginId, Double psnrAvg, Double ssimAvg, String task) {
        String days = Instant.now().toString(); // Get current time as ISO 8601 string
        ComparisonResult result = new ComparisonResult(userId, requestId, loginId, psnrAvg, ssimAvg, task, days); // Create new result object
        comparisionResultRepository.save(result); // Save result to MongoDB
    }

    // Save task name to MongoDB if it doesn't exist
    @Override
    public void saveTaskRepository(String token, String taskName) {
        boolean exists = taskMongoRepository.existsByTaskName(taskName); // Check if task name already exists
        if (!exists) {
            Task task = new Task(taskName); // Create new task entity
            taskMongoRepository.save(task); // Save task to MongoDB
        }
    }
}
