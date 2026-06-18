package com.trevix.property_management.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PropertyCreateRequest {

    @NotBlank(message = "Property name is required")
    private String name;

    @NotBlank(message = "Address is required")
    private String address;

    private String timezone;       
    private String curfewTime;    
    private String roomRules;
}