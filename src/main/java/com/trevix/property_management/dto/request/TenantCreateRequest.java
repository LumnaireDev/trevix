package com.trevix.property_management.dto.request;

import java.time.LocalDate;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TenantCreateRequest {
    private String emergencyContactName;

    @NotBlank(message = "Emergency contact phone is required")
    private String emergencyContactPhone;

    private String emergencyContactRelation;
    private LocalDate moveInDate;
}