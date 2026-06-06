package com.trevix.property_management.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class PropertyCreateRequest {
    
    @NotBlank(message = "Property name is required")
    private String propertyName;
    
    @NotBlank(message = "Address is required")
    private String address;
    
    private String city;
    private String state;
    private String zipCode;
    private String country;
    
    @NotNull(message = "Total units is required")
    private Integer totalUnits;
    
    private BigDecimal monthlyRent;
    private UUID adminUserId; // Owner/Manager
}