package com.yourorg.grading.adapter.out.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.yourorg.grading.domain.entity.ComparisonResult;

// Repository interface for managing ComparisonResult documents in MongoDB
// Extends MongoRepository to inherit basic CRUD operations
public interface ComparisonResultRepository extends MongoRepository<ComparisonResult, String> {
    // No additional methods are defined; default CRUD operations are used
}
