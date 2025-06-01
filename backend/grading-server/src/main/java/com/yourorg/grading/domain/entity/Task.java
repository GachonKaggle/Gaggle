package com.yourorg.grading.domain.entity;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nonapi.io.github.classgraph.json.Id;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

// This class maps to the "task" collection in MongoDB
@Document(collection = "task")
public class Task {

    // Unique identifier for each task document
    @Id
    private String taskId;

    // Task name must be unique in the collection (enforced by index)
    @Indexed(unique = true)
    private String taskName;

    // Custom constructor for initializing only taskName (MongoDB will generate taskId)
    public Task(String taskName) {
        this.taskName = taskName;
    }
}
