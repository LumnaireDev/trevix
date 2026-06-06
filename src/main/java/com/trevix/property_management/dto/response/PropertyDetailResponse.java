package com.trevix.property_management.dto.response;

import com.trevix.property_management.enums.PropertyStatus;
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
    private String timezone;
    private String curfewTime;
    private String roomRules;
    private PropertyStatus status;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private UUID adminId;
    private String adminName;
    private String adminEmail;
    
    // Statistics
    private Integer totalRooms;
    private Long occupiedRooms;
    private Long availableRooms;
    private Integer totalOccupancy;
    private Double occupancyRate;
}