package com.yourorg.user_info.port.out.auth; // Package declaration for outbound authentication port

import com.yourorg.user_info.domain.entity.User; // Import User entity

// This interface defines the contract for accessing user data from the persistence layer
public interface AuthRepositoryPort {

    // Save a user entity to the database and return the saved instance
    User save(User user);

    // Find a user by their loginId and return the corresponding User entity (or null if not found)
    User findByLoginId(String loginId);
}
