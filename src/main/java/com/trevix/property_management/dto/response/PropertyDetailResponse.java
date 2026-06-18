package com.trevix.property_management.dto.response;

import com.trevix.property_management.enums.PropertyStatus;
import com.trevix.property_management.enums.PropertyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyDetailResponse {

    private UUID propertyId;
    private String name;
    private String address;
    private PropertyType type;
    private String timezone;
    private String roomRules;
    private PropertyStatus status;
    private UUID ownerId;
    private String ownerName;
    private String ownerEmail;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    // Statistics
    private int totalRooms;
    private long occupiedRooms;
    private long availableRooms;
    private double occupancyRate;
}