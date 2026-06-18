package com.trevix.property_management.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class UtilityMeterReadingResponse {
    private UUID id;
    private LocalDate readingDate;
    private BigDecimal electricityKwh;
    private BigDecimal waterM3;
    private String notes;
    private UUID submittedById;
    private String submittedByName;    // User display name
    private OffsetDateTime createdAt;
}