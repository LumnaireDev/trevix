package com.trevix.property_management.dto.response;

import lombok.Data;
import com.trevix.property_management.enums.MaintenanceStatus;
import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.List;

@Data
public class MaintenanceRequestResponse {
    private UUID id;
    private UUID tenantId;
    private String tenantName;
    private String title;
    private String description;
    private MaintenanceStatus status;
    private String priority;
    private String aiClassification;
    private UUID assignedToId;
    private String assignedToName;     // StaffProfile display name
    private OffsetDateTime completedAt;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private List<String> photoUrls;    // flattened from MaintenanceRequestPhoto
}