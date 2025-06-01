package com.yourorg.user_info; // Declares the package for this application

import org.springframework.boot.SpringApplication; // Imports Spring Boot application launcher
import org.springframework.boot.autoconfigure.SpringBootApplication; // Enables auto-configuration and component scanning

@SpringBootApplication // Annotation to mark this class as the entry point of a Spring Boot application
public class UserInfoServerApplication {
    public static void main(String[] args) {
        // Launches the Spring Boot application
        SpringApplication.run(UserInfoServerApplication.class, args);
    }
}
