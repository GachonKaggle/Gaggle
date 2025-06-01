package com.yourorg.grading; // Package declaration

import org.springframework.boot.SpringApplication; // Spring Boot launcher class
import org.springframework.boot.autoconfigure.SpringBootApplication; // Annotation for auto-configuration and component scanning

@SpringBootApplication // Marks this class as a Spring Boot application entry point
public class GradingServerApplication {

    public static void main(String[] args) {
        // Launches the Spring Boot application
        SpringApplication.run(GradingServerApplication.class, args);
    }
}
