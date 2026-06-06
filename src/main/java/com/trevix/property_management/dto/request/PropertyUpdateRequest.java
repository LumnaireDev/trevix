package com.trevix.property_management.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import com.trevix.property_management.enums.PropertyStatus;
import java.time.LocalTime;

@Data
public class PropertyUpdateRequest {

    @NotBlank(message = "Property name is required")
    private String name;

    @NotBlank(message = "Address is required")
    private String address;

    private String timezone;

    private LocalTime curfewTime;

    private String roomRules;

    private PropertyStatus status;
}