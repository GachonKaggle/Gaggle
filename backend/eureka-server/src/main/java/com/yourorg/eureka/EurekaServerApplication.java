package com.yourorg.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication  // Marks this class as a Spring Boot application (includes @Configuration, @EnableAutoConfiguration, @ComponentScan)
@EnableEurekaServer     // Enables Eureka Server, making this application act as a service registry
public class EurekaServerApplication {
    
    // Main method: the entry point of the Spring Boot application
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args); // Launch the application
    }
}
