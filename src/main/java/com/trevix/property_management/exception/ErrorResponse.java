package com.trevix.property_management.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private String errorCode;
    private String message;
    private int statusCode;
    private String status;
    private String path;
    private LocalDateTime timestamp;
    private Map<String, String> validationErrors;

    public static ErrorResponse of(String errorCode, String message, int statusCode, String path) {
        return ErrorResponse.builder()
                .errorCode(errorCode)
                .message(message)
                .statusCode(statusCode)
                .status(getStatusText(statusCode))
                .path(path)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static ErrorResponse ofValidation(String errorCode, String message, Map<String, String> errors, String path) {
        return ErrorResponse.builder()
                .errorCode(errorCode)
                .message(message)
                .statusCode(400)
                .status("BAD_REQUEST")
                .validationErrors(errors)
                .path(path)
                .timestamp(LocalDateTime.now())
                .build();
    }

    private static String getStatusText(int statusCode) {
        return switch (statusCode) {
            case 400 -> "BAD_REQUEST";
            case 401 -> "UNAUTHORIZED";
            case 403 -> "FORBIDDEN";
            case 404 -> "NOT_FOUND";
            case 409 -> "CONFLICT";
            case 422 -> "UNPROCESSABLE_ENTITY";
            case 500 -> "INTERNAL_SERVER_ERROR";
            default -> "ERROR";
        };
    }
}