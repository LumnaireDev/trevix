package com.trevix.property_management.dto.request;

import jakarta.validation.constraints.NotNull;
import com.trevix.property_management.enums.MaintenanceStatus;
import lombok.Data;

@Data
public class MaintenanceStatusUpdateRequest {

    @NotNull(message = "Status is required")
    private MaintenanceStatus status;
}
