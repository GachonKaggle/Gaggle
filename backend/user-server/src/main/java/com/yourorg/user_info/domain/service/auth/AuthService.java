package com.yourorg.user_info.domain.service.auth; // Package declaration for the authentication service

import com.yourorg.user_info.adapter.in.dto.LoginResponsedto; // Import DTO used for login response
import com.yourorg.user_info.domain.entity.User; // Import User entity
import com.yourorg.user_info.port.in.auth.AuthPort; // Import inbound port for authentication
import com.yourorg.user_info.port.out.auth.AuthRepositoryPort; // Import outbound port for repository interaction
import com.yourorg.user_info.util.JwtProvider; // Import utility class for generating JWT tokens
import org.springframework.stereotype.Service; // Marks this class as a Spring service component
import java.util.UUID; // Import UUID class for generating unique user IDs

@Service // Register this class as a Spring service bean
public class AuthService implements AuthPort { // AuthService implements the AuthPort interface

    private final AuthRepositoryPort authRepositoryPort; // Port to interact with the repository layer
    private final JwtProvider jwtProvider; // Utility to handle JWT token generation

    // Constructor injection for dependencies
    public AuthService(AuthRepositoryPort authRepositoryPort, JwtProvider jwtProvider) {
        this.authRepositoryPort = authRepositoryPort; // Assign repository port
        this.jwtProvider = jwtProvider; // Assign JWT provider
    }

    @Override
    public User signup(String loginId, String password, String role) {
        // Check if a user with the given login ID already exists
        if (authRepositoryPort.findByLoginId(loginId) != null) {
            throw new RuntimeException("이미 존재하는 사용자입니다."); // Throw exception if user already exists
        }

        // If role is not provided, assign default role "USER"
        if (role == null) {
            role = "USER"; // Default role
        }

        // 실제로는 password 암호화 필요! // Password should be encrypted in real applications
        User user = new User(); // Create a new User instance
        user.setUserId(UUID.randomUUID().toString()); // Generate and set a unique UUID for the user
        user.setLoginId(loginId); // Set login ID
        user.setPassword(password); // Set password
        user.setRole(role); // Set role

        return authRepositoryPort.save(user); // Save user to the repository and return the saved user
    }

    @Override
    public LoginResponsedto login(String loginId, String password) {
        // Find user by login ID
        User user = authRepositoryPort.findByLoginId(loginId);

        // Validate password
        if (user == null || !user.getPassword().equals(password)) {
            throw new RuntimeException("로그인 실패"); // Throw exception if login fails
        }

        // Generate JWT token with user ID and role
        String token = jwtProvider.generateToken(user.getUserId(), user.getRole());

        // Create response DTO with token
        LoginResponsedto dto = new LoginResponsedto(token);

        // JWT 토큰 발급 // Return JWT token response
        return dto;
    }
}
