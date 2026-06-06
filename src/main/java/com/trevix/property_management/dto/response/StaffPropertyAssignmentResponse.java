package com.trevix.property_management.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class StaffPropertyAssignmentResponse {
    private UUID id;
    private UUID staffUserId;
    private String staffName;
    private UUID propertyId;
    private String propertyName;
    private OffsetDateTime assignedAt;
}