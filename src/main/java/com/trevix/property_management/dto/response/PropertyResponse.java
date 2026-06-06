package com.trevix.property_management.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class PropertyResponse {
    private UUID id;
    private String propertyName;
    private String address;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    private Integer totalUnits;
    private BigDecimal monthlyRent;
    private UUID adminUserId;
    private String adminName;
    private Integer occupiedUnits;
    private Integer availableUnits;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
