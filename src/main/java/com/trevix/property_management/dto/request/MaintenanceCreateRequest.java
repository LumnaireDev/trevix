package com.trevix.property_management.dto.request;

import java.util.UUID;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MaintenanceCreateRequest {

    @NotNull(message = "Property ID is required")
    private UUID propertyId;

    @NotNull(message = "Room ID is required")
    private UUID roomId;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    private String priority = "medium"; // low, medium, high
}