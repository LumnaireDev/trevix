package com.trevix.property_management.dto.request;

import java.time.OffsetDateTime;
import java.util.UUID;
import com.trevix.property_management.enums.MaintenanceStatus;
import lombok.Data;

@Data
public class MaintenanceRequestResponse {
    private UUID id;
    private UUID propertyId;
    private String propertyName;
    private UUID roomId;
    private String roomNumber;
    private UUID tenantId;
    private String tenantName;
    private String title;
    private String description;
    private MaintenanceStatus status;
    private String priority;
    private OffsetDateTime completedAt;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}