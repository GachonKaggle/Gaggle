package com.yourorg.grading.adapter.out.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.yourorg.grading.domain.entity.ComparisonResult;

public interface ComparisonResultRepository extends MongoRepository<ComparisonResult, String> {
}
