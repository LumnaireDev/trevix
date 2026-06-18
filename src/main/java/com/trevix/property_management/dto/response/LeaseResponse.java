package com.trevix.property_management.dto.response;

import lombok.Data;
import com.trevix.property_management.enums.LeaseStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class LeaseResponse {
    private UUID id;
    private UUID tenantId;
    private String tenantName;         // tenant.fullName or similar
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal rentAmount;
    private BigDecimal depositAmount;
    private String contractUrl;
    private LeaseStatus status;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}