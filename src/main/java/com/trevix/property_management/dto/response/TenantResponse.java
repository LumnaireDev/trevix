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
public class TenantResponse {
    private UUID userId;
    private UserResponse user;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String emergencyContactRelation;
    private BigDecimal scorecardScore;
    private String scorecardGrade;
    private LocalDate moveInDate;
    private Boolean isActive;
}