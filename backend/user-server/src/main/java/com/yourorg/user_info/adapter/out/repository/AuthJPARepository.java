package com.yourorg.user_info.adapter.out.repository; // Define the package for repository interfaces

import com.yourorg.user_info.domain.entity.User; // Import the User entity
import org.springframework.data.mongodb.repository.MongoRepository; // Import MongoRepository interface for MongoDB operations

// Define a repository interface for User entity, using MongoDB with User ID of type String
public interface AuthJPARepository extends MongoRepository<User, String> {
    
    // Custom query method to find a user by loginId field
    User findByLoginId(String loginId);
}
