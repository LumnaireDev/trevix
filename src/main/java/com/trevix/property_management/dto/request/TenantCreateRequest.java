package com.trevix.property_management.dto.request;

import java.time.LocalDate;
import java.util.UUID;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TenantCreateRequest {
    @NotNull(message = "User ID is required")
    private UUID userId;
    
    private String emergencyContactName;
    
    @NotBlank(message = "Emergency contact phone is required")
    private String emergencyContactPhone;
    
    private String emergencyContactRelation;

    private String email;

    private String password;
    
    private LocalDate moveInDate;
}