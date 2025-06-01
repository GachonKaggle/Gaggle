package com.yourorg.grading.adapter.out.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.yourorg.grading.domain.entity.Task;

// Repository interface for Task documents in MongoDB
// Inherits basic CRUD operations from MongoRepository
public interface TaskMongoRepository extends MongoRepository<Task, String> {

    // Custom query method to check if a task exists by its name
    boolean existsByTaskName(String taskName);
}
