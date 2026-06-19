package com.trevix.property_management.dto.request;

import lombok.Data;
import java.time.LocalDate;

@Data
public class TenantUpdateRequest {
    private String fullName;
    private String phone;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String emergencyContactRelation;
    private LocalDate moveInDate;
    private Boolean isActive;
}