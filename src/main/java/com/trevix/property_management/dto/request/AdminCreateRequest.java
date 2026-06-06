package com.trevix.property_management.dto.request;

import com.trevix.property_management.enums.SubscriptionPlan;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AdminCreateRequest {
    
    @NotBlank(message = "Company name is required")
    private String companyName;
    
    private String taxId;
    
    private SubscriptionPlan subscriptionPlan;
}