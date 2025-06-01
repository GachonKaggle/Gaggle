package com.yourorg.user_info.adapter.out.persistence; // Define the package for persistence adapters

import org.springframework.stereotype.Component; // Import Spring's @Component annotation

import com.yourorg.user_info.adapter.out.repository.UserJPARepository; // Import the JPA repository for User
import com.yourorg.user_info.domain.entity.User; // Import the User entity
import com.yourorg.user_info.port.out.persistence.UserWritePort; // Import the outbound port interface for writing users
import lombok.RequiredArgsConstructor; // Import Lombok annotation to generate constructor for final fields

@Component // Mark this class as a Spring-managed component
@RequiredArgsConstructor // Automatically generate constructor for final fields
public class UserWriteAdapter implements UserWritePort { // Implement the UserWritePort interface
    
    private final UserJPARepository userJPARepository; // Inject the JPA repository to handle persistence

    @Override
    public User saveUser(User user) { // Implement the saveUser method defined in the interface
        return userJPARepository.save(user); // Use the JPA repository to save the User entity and return it
    }
}
