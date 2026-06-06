package com.trevix.property_management.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.UUID;

@Data
public class StaffPropertyAssignmentRequest {
    
    @NotNull(message = "Staff user ID is required")
    private UUID staffUserId;
    
    @NotNull(message = "Property ID is required")
    private UUID propertyId;
}