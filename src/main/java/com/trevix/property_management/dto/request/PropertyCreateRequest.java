package com.trevix.property_management.dto.request;

import com.trevix.property_management.enums.PropertyType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class PropertyCreateRequest {

    @NotBlank(message = "Property name is required")
    private String name;

    @NotBlank(message = "Address is required")
    private String address;

    @NotNull(message = "Property type is required")
    private PropertyType type; 

    private String timezone;       
    private String roomRules;
}