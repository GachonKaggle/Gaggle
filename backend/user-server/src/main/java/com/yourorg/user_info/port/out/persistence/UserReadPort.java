package com.yourorg.user_info.port.out.persistence; // Package declaration for user persistence output port

import com.yourorg.user_info.domain.entity.User; // Import User entity
import java.util.Optional; // Import Optional to handle possible null values safely

// This interface defines a read-only access contract for user data in the persistence layer
public interface UserReadPort {

    // Find a user by their loginId and return an Optional<User> (empty if not found)
    Optional<User> findByLoginId(String loginId); // "load" changed to "find" for naming consistency
}
