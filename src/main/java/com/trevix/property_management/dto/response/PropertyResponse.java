package com.trevix.property_management.dto.response;

import lombok.Builder;
import lombok.Data;
import com.trevix.property_management.enums.PropertyStatus;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class PropertyResponse {
    private UUID propertyId;
    private String name;
    private String address;
    private String timezone;
    private String curfewTime;
    private String roomRules;
    private PropertyStatus status;
    private UUID adminId;
    private String adminName;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}