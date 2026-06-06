package com.trevix.property_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantDetailResponse {

    private UUID userId;
    private String fullName;
    private String email;
    private String phone;

    // Emergency Contact
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String emergencyContactRelation;

    // Scorecard
    private BigDecimal scorecardScore;
    private String scorecardGrade;

    // Status
    private LocalDate moveInDate;
    private Boolean isActive;
}