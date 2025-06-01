package com.yourorg.leaderboard.domain.entity; // Package declaration for the domain entity layer

import org.springframework.data.mongodb.core.index.Indexed; // Used to mark a field for indexing in MongoDB
import org.springframework.data.mongodb.core.mapping.Document; // Indicates this class is mapped to a MongoDB document

import lombok.AllArgsConstructor; // Lombok annotation to generate a constructor with all fields
import lombok.Getter; // Lombok annotation to generate getters for all fields
import lombok.NoArgsConstructor; // Lombok annotation to generate a no-argument constructor
import lombok.Setter; // Lombok annotation to generate setters for all fields
import nonapi.io.github.classgraph.json.Id; // Annotation used to mark a field as the ID in some serialization libraries (not standard MongoDB ID)

// Lombok annotations to auto-generate boilerplate code
@Getter
@Setter
@AllArgsConstructor // Constructor with all fields
@NoArgsConstructor // Default no-arg constructor
@Document(collection = "task") // Maps this class to the "task" collection in MongoDB
public class Task {

    @Id // Marks this field as the document ID (not the standard Spring version; likely for serialization)
    private String taskId; // Unique ID for the task document

    @Indexed(unique = true) // Create a unique index on the taskName field
    private String taskName; // Name of the task (must be unique)

    // Custom constructor that only takes taskName
    public Task(String taskName) {
        this.taskName = taskName; // Assign task name to the field
    }
}
