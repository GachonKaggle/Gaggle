package com.yourorg.user_info.adapter.out.repository; // Define the package for repository interfaces

import java.util.Optional; // Import Optional class to handle nullable return types
import com.yourorg.user_info.domain.entity.User; // Import the User entity
import org.springframework.data.mongodb.repository.MongoRepository; // Import MongoRepository for MongoDB access

// Define a repository interface for User entity with String as ID type
public interface UserJPARepository extends MongoRepository<User, String> {

    // Custom query method to find a user by their loginId, returns Optional<User>
    Optional<User> findByLoginId(String findByLoginId);
}
