package com.yourorg.grading.adapter.out.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.yourorg.grading.domain.entity.Task;

public interface TaskMongoRepository extends MongoRepository<Task, String> {
    boolean existsByTaskName(String taskName); 
}
