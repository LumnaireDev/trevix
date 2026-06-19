package com.trevix.property_management.dto.response;

import lombok.Data;
import com.trevix.property_management.enums.RoomStatus;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class RoomDetailResponse {
    private UUID id;
    private UUID propertyId;
    private String propertyName;
    private String roomNumber;
    private Integer floor;
    private BigDecimal baseRent;
    private BigDecimal waterFee;
    private BigDecimal electricityFee;
    private Integer capacity;
    private Integer currentOccupancy;
    private String description;
    private RoomStatus status;
    private Boolean isActive;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    // Only what has UI backing
    private List<MaintenanceRequestResponse> maintenanceRequests;
}