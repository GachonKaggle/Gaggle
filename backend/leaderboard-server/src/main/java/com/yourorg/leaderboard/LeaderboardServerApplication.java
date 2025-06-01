package com.yourorg.leaderboard; // Define the base package for the leaderboard application

import org.springframework.boot.SpringApplication; // Import Spring Boot launcher class
import org.springframework.boot.autoconfigure.SpringBootApplication; // Import annotation for auto-configuration

@SpringBootApplication // Enable component scanning, configuration, and auto-configuration
public class LeaderboardServerApplication { // Main class for the leaderboard Spring Boot application

    public static void main(String[] args) { // Entry point of the application
        SpringApplication.run(LeaderboardServerApplication.class, args); // Launch the Spring Boot application
    }
}
