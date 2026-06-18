package com.trevix.property_management.dto.request;

import lombok.Data;
import com.trevix.property_management.enums.PropertyStatus;
import com.trevix.property_management.enums.PropertyType;

@Data
public class PropertyUpdateRequest {

    private String name;
    private String address;
    private PropertyType type;
    private String timezone;
    private String roomRules;
    private PropertyStatus status;
}