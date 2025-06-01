package com.yourorg.user_info.adapter.out.auth; // Define the package for authentication persistence adapter

import com.yourorg.user_info.domain.entity.User; // Import User entity
import com.yourorg.user_info.port.out.auth.AuthRepositoryPort; // Import port interface for persistence operations
import org.springframework.stereotype.Repository; // Annotation to mark this as a Spring Repository
import com.yourorg.user_info.adapter.out.repository.AuthJPARepository; // Import JPA repository interface

@Repository // Marks this class as a Spring repository bean
public class AuthRepositoryAdapter implements AuthRepositoryPort { // Implements the AuthRepositoryPort interface

    private final AuthJPARepository authJpaRepository; // JPA repository for User entity

    public AuthRepositoryAdapter(AuthJPARepository authJpaRepository) { // Constructor for dependency injection
        this.authJpaRepository = authJpaRepository; // Assign injected repository
    }

    @Override
    public User save(User user) { // Saves a User entity to the database
        return authJpaRepository.save(user); // Delegate to JPA repository
    }

    @Override
    public User findByLoginId(String loginId) { // Finds a User by loginId
         return authJpaRepository.findByLoginId(loginId); // Delegate to JPA repository
    }
}
