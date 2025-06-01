package com.yourorg.grading.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

// This class maps to the "user" collection in MongoDB
@Document(collection = "user")
public class User {

    // Unique identifier for the user (MongoDB's primary key)
    @Id
    private String userId;

    // Unique login ID for the user, must be unique in the collection
    @Indexed(unique = true)
    private String loginId;

    // Password for authentication
    private String password;

    // User role (e.g., "USER", "ADMIN")
    private String role;
}
