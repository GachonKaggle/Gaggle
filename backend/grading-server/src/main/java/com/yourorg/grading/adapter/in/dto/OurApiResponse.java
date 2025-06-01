package com.yourorg.grading.adapter.in.dto;  // Package declaration

import lombok.AllArgsConstructor;  // Lombok annotation to generate a constructor with all arguments
import lombok.Setter;              // Lombok annotation to generate setter methods for fields
import lombok.Getter;              // Lombok annotation to generate getter methods for fields
import io.swagger.v3.oas.annotations.media.Schema;  // Swagger annotation for API schema documentation

@Getter  // Lombok will generate getter methods for all fields
@Setter  // Lombok will generate setter methods for all fields
@AllArgsConstructor  // Lombok will generate a constructor with all fields as parameters
@Schema(description = "API 공통 응답")  // Describes this class in Swagger as a common API response
public class OurApiResponse<T> {  // Generic response wrapper class for API responses
    @Schema(description = "상태", example = "success, fail")  // Describes the status field in API docs
    private String status;  // Status of the response, e.g., "success" or "fail"

    @Schema(description = "데이터")  // Describes the data field in API docs
    private T data;  // Generic data payload of the response

    @Schema(description = "응답 메시지", nullable = true)  // Describes the message field in API docs
    private String message;  // Optional message accompanying the response
}
