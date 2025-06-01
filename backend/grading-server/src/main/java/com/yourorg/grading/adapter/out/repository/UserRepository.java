package com.yourorg.grading.adapter.out.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.yourorg.grading.domain.entity.User;

public interface UserRepository extends MongoRepository<User, String> {
}
