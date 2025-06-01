package com.yourorg.user_info.domain.entity; // Define the package for the User domain entity

import lombok.AllArgsConstructor; // Lombok annotation to generate an all-args constructor
import lombok.Getter; // Lombok annotation to generate getters
import lombok.NoArgsConstructor; // Lombok annotation to generate a no-args constructor
import lombok.Setter; // Lombok annotation to generate setters
import org.springframework.data.annotation.Id; // Annotation to mark the primary key field for MongoDB
import org.springframework.data.mongodb.core.mapping.Document; // Annotation to map this class to a MongoDB collection
import org.springframework.data.mongodb.core.index.Indexed; // Annotation to define indexing on MongoDB fields

@Getter // Generate getter methods for all fields
@Setter // Generate setter methods for all fields
@AllArgsConstructor // Generate constructor with all fields as parameters
@NoArgsConstructor // Generate constructor with no parameters
@Document(collection = "user") // Map this class to the "user" collection in MongoDB
public class User {

    @Id // Mark this field as the primary key for MongoDB
    private String userId; // Unique identifier for the user

    @Indexed(unique = true) // Ensure that loginId is unique and indexed in the database
    private String loginId; // Login ID for the user (used for authentication)

    private String password; // 비밀번호 필드 // Password field for user login

    private String role; // User role (e.g., ADMIN, USER)
}
