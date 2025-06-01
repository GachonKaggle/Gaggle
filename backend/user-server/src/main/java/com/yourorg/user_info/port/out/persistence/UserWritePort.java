package com.yourorg.user_info.port.out.persistence; // Package declaration for persistence output port

import com.yourorg.user_info.domain.entity.User; // Import the User entity class

// This interface defines a write-only access contract for user data in the persistence layer
public interface UserWritePort {

    // Save a User entity to the database and return the saved User instance
    User saveUser(User user);
}
