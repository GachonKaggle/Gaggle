package com.yourorg.user_info.adapter.out.persistence; // Define the package for persistence adapter

import org.springframework.stereotype.Component; // Import Spring's @Component annotation
import lombok.RequiredArgsConstructor; // Import Lombok annotation to generate constructor

import com.yourorg.user_info.domain.entity.User; // Import User entity
import com.yourorg.user_info.port.out.persistence.UserReadPort; // Import outbound port interface
import com.yourorg.user_info.adapter.out.repository.UserJPARepository; // Import JPA repository for User

import java.util.Optional; // Import Optional for null-safe return values

@Component // Marks this class as a Spring-managed component
@RequiredArgsConstructor // Generates a constructor for final fields
public class UserReadAdapter implements UserReadPort { // Implements the UserReadPort interface

    private final UserJPARepository userRepository; // JPA repository to access User entities

    @Override
    public Optional<User> findByLoginId(String loginId) { // Implements method to find user by loginId
        return userRepository.findByLoginId(loginId); // Delegate the search to the JPA repository
    }
}
