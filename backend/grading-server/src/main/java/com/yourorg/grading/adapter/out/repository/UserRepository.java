package com.yourorg.grading.adapter.out.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.yourorg.grading.domain.entity.User;

// Repository interface for User documents in MongoDB
// Inherits basic CRUD operations (save, findById, findAll, delete, etc.)
public interface UserRepository extends MongoRepository<User, String> {
    // No additional methods defined yet — basic CRUD is provided by MongoRepository
}
