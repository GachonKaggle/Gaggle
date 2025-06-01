package com.yourorg.user_info.port.in.auth; // Package declaration for inbound authentication port

import com.yourorg.user_info.domain.entity.User; // Import User entity
import com.yourorg.user_info.adapter.in.dto.LoginResponsedto; // Import DTO used for login response

// This interface defines the inbound authentication use cases
public interface AuthPort {

    // Method for user signup. Takes loginId, password, and role as input and returns the created User entity
    User signup(String loginId, String password, String role);

    // Method for user login. Takes loginId and password as input and returns a JWT token response DTO
    LoginResponsedto login(String loginId, String password);
}
