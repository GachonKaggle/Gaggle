package com.yourorg.leaderboard.adapter.in.dto;

import lombok.AllArgsConstructor; // Lombok: generates constructor with all arguments
import lombok.Setter; // Lombok: generates setter methods
import lombok.Getter; // Lombok: generates getter methods
import io.swagger.v3.oas.annotations.media.Schema; // Swagger annotation for API documentation

@Getter // Lombok annotation to generate getter methods for all fields
@Setter // Lombok annotation to generate setter methods for all fields
@AllArgsConstructor // Lombok annotation to generate constructor with all fields as parameters
@Schema(description = "API 공통 응답") // Swagger description: "API common response" (left in Korean as instructed)
public class OurApiResponse<T> { // Generic class for wrapping API responses

    @Schema(description = "상태", example = "success, fail") // Swagger: status of the response (Korean left unchanged)
    private String status; // Indicates whether the request was successful or failed (e.g., "success", "fail")

    @Schema(description = "데이터") // Swagger: data field (Korean left unchanged)
    private T data; // The actual payload/data of the response (generic type)

    @Schema(description = "응답 메시지", nullable = true) // Swagger: response message (Korean left unchanged)
    private String message; // Optional message for additional information (can be null)
}
