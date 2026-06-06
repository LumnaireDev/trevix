package com.trevix.property_management.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class TenantScorecardResponse {
    private UUID userId;
    private String fullName;
    private String email;
    private BigDecimal scorecardScore;
    private String scorecardGrade;
    private String recommendation; // e.g., "Excellent Tenant", "Needs Improvement"
}
