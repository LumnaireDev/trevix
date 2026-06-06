package com.trevix.property_management.dto.request;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TenantUpdateRequest {
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String emergencyContactRelation;
    private BigDecimal scorecardScore;
    private String scorecardGrade;
    private LocalDate moveInDate;
    private Boolean isActive;
}